package com.netty100.service.impl;

import com.google.common.collect.Maps;
import com.netty100.config.LocalCacheManager;
import com.netty100.entity.AppConfig;
import com.netty100.pojo.dto.OnlineDebugDto;
import com.netty100.service.OnlineDebugService;
import com.netty100.service.RegistrationDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author why
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OnlineDebugServiceImpl implements OnlineDebugService {

    private final RegistrationDetailService registrationDetailService;

    private final Map<String, String> maps = Maps.newHashMap();

    private final LocalCacheManager localCacheManager;

    @PostConstruct
    public void postConstruct() {
        maps.put("101", "客户端连接成功");
        maps.put("102", "客户端心跳确认");
        maps.put("103", "客户端消息处理失败");
        maps.put("104", "客户端连接正常断开");
        maps.put("105", "客户端连接异常断开");
        maps.put("106", "客户端连接心跳断开");
        maps.put("107", "客户端连接未认证");
        maps.put("108", "客户端认证失败，设备密码不能为空");
        maps.put("109", "客户端认证失败，设备未注册");
        maps.put("110", "客户端认证失败，设备密码错误");
        maps.put("111", "客户端认证失败，静态服务器访问失败");
        maps.put("112", "客户端配置失败，消息协议客户端来源不能小于0");
        maps.put("113", "客户端配置失败，消息协议服务器来源不能小于0");
        maps.put("114", "客户端配置失败，消息协议未配置");
        maps.put("115", "服务器不在线");
        maps.put("116", "用户强制退出，用户已在其他pad登录");
        maps.put("117", "客户端配置不正确，消息与配置不一致");
        maps.put("201", "服务器连接成功");
        maps.put("202", "服务器心跳确认");
        maps.put("203", "服务器消息处理失败");
        maps.put("204", "服务器连接正常断开");
        maps.put("205", "服务器连接异常断开");
        maps.put("900", "未知错误");
        maps.put("901", "消息不合法，无法解析");
        maps.put("902", "消息不能为空");
        maps.put("903", "此消息方式未实现");
    }

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private static final ExpiringMap<String, Socket> SOCKETS =
            ExpiringMap.builder().expiration(270, TimeUnit.SECONDS)
                    .expirationPolicy(ExpirationPolicy.ACCESSED)
                    .build();

    static {
        SOCKETS.addExpirationListener((k, v) -> IOUtils.closeQuietly(v));
    }

    @Override
    public String onlineDebug(OnlineDebugDto dto) {
        val key = generateKey(dto);
        String response = "消息发送失败";
        val socket = SOCKETS.get(key);
        if (Objects.isNull(socket)) {
            return "连接尚未创建或空闲超时自动断开,请重新创建连接";
        }
        try {
            dto.setMessageWay((byte) 3);
            val outputStream = socket.getOutputStream();
            outputStream.write(formMessage(dto));
            outputStream.flush();
            val dataInputStream = new DataInputStream(socket.getInputStream());
            response = abstractMessage(dataInputStream);
        } catch (IOException e) {
            log.error("在线调试发送消息失败,连接地址:{}", dto.getLocalAddress());
        }
        if (maps.containsKey(response)) {
            response = maps.get(response);
        }
        return response;
    }

    @Override
    public String connect(OnlineDebugDto dto) {
        return getSocket(dto);
    }

    @Override
    public String disconnect(OnlineDebugDto dto) {
        val key = generateKey(dto);
        if (!SOCKETS.containsKey(key)) {
            return "连接不存在";
        } else {
            try {
                writeLock.lock();
                IOUtils.closeQuietly(SOCKETS.get(key));
                SOCKETS.remove(key);
            } finally {
                writeLock.unlock();
            }
        }
        return "连接断开成功";
    }

    private String getSocket(OnlineDebugDto dto) {
        String response = "服务器连接失败";
        val success = "101";
        val key = generateKey(dto);
        Socket socket = SOCKETS.get(key);
        if (Objects.isNull(socket)) {
            try {
                writeLock.lock();
                socket = SOCKETS.get(key);
                if (Objects.isNull(socket)) {
                    val code = registrationDetailService.getDevicePwd(dto.getDeviceId(), dto.getUserId());
                    if (Objects.isNull(code)) {
                        return "未找到注册码";
                    }
                    val appConfigs = localCacheManager.appConfigs();
                    final Optional<AppConfig> optional = appConfigs.stream().filter(x -> Objects.equals(x.getMessageSource(), dto.getMessageSource()) && Objects.equals(x.getMessageDest(), dto.getMessageDest())).findFirst();
                    if (!optional.isPresent()) {
                        return "消息协议配置未找到";
                    }
                    val localAddress = dto.getLocalAddress();
                    val localPort = dto.getLocalPort();
                    socket = new Socket(localAddress, Integer.parseInt(localPort));
                    dto.setMessageWay((byte) 1);
                    dto.setMessage(code);
                    val outputStream = socket.getOutputStream();
                    outputStream.write(formMessage(dto));
                    outputStream.flush();
                    val dataInputStream = new DataInputStream(socket.getInputStream());
                    response = abstractMessage(dataInputStream);
                    if (success.equals(response)) {
                        SOCKETS.put(key, socket);
                        return "连接成功";
                    } else {
                        if (maps.containsKey(response)) {
                            response = maps.get(response);
                        }
                        IOUtils.closeQuietly(socket);
                        return response;
                    }
                } else {
                    return "连接已存在";
                }
            } catch (Exception e) {
                log.error("服务器连接失败,远程连接地址:{}", dto.getLocalAddress(), e);
                if (Objects.nonNull(socket)) {
                    IOUtils.closeQuietly(socket);
                }
                return response;
            } finally {
                writeLock.unlock();
            }
        }
        return "连接已存在";
    }

    private String generateKey(OnlineDebugDto dto) {
        return dto.getDeviceId() + "@" +
                dto.getUserId() + "@" +
                dto.getLocalAddress() + "@" +
                dto.getLocalPort();
    }

    private byte[] formMessage(OnlineDebugDto dto) {
        val bytes = dto.getMessage().getBytes(StandardCharsets.UTF_8);
        val buffer = ByteBuffer.allocate(33 + bytes.length);
        buffer.putInt(29 + bytes.length);
        buffer.put(dto.getMessageWay());
        buffer.put(dto.getMessageSource());
        buffer.put(dto.getMessageDest());
        buffer.put(dto.getMessageType());
        buffer.put(dto.getMessageSerialize());
        buffer.putLong(dto.getMessageId());
        buffer.putLong(dto.getDeviceId());
        buffer.putLong(dto.getUserId());
        buffer.put(bytes);
        return buffer.array();
    }

    private String abstractMessage(DataInputStream dataInputStream) {
        String response = null;
        try {
            final int length = dataInputStream.readInt();
            byte[] b = new byte[length];
            final int readBytes = dataInputStream.read(b);
            if (Objects.equals(readBytes, length)) {
                response = new String(b, 29, length - 29);
            }
        } catch (IOException e) {
            log.error("netty响应消息读取失败", e);
            response = "netty响应消息读取失败";
        }
        return response;
    }
}
