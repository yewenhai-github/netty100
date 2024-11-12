package com.netty100.netty.codec.consumer.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.netty100.netty.codec.common.entity.TopeRequestBodyProto2;
import com.netty100.netty.codec.common.entity.TopeRequestBody;
import com.netty100.netty.codec.common.entity.TopeRequestBodyProto3;
import com.netty100.netty.codec.consumer.constants.CodecErrorCode;
import com.netty100.netty.codec.consumer.service.WhyMessageConsumerService;
import com.netty100.common.exception.CommonException;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessageFixedHeader;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.client.service.WhyNettySdkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/**
 * <p>Description:</p>
 * <p>Copyright: Copyright (c)2018</p>
 * <p>Company: why</p>
 * <P>Created Date :2021-11-21</P>
 * <P>@version 1.0</P>
 */
@Slf4j
@Service
public class WhyMessageConsumerServiceImpl implements WhyNettySdkService<byte[]>, WhyMessageConsumerService {

    private static HashSet<String> logResponseCode = new HashSet<>();
    static {
        logResponseCode.add(String.valueOf(ResponseCode.Rep103.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep104.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep105.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep106.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep107.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep108.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep109.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep110.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep111.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep112.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep113.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep114.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep115.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep116.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep117.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep118.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep203.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep204.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep205.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep300.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep900.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep901.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep902.getCode()));
        logResponseCode.add(String.valueOf(ResponseCode.Rep903.getCode()));
    }

    @Override
    public void doCommand(byte[] body, WhyMessageFixedHeader header) {
        Long userId = header.getUserId();
        String uri = "";
        String token = "";
        Object data = null;
        WhyMessageConsumerService whyMessageConsumerService;
        try {
            switch (header.getMessageSerialize()){
                case 1:
                    //内核与各个客户端交互的序列化格式，不开放给其他业务使用，定义的响应码见：enum ResponseCode
                    String str = new String(body, StandardCharsets.UTF_8);
                    String[] arr = StringUtils.split(str, "\\|");

                    if(logResponseCode.contains(str) && SysUtility.isEmpty(arr)){
                        log.info("内核推送消息：{}", str+"-"+ ResponseCode.getMassageByCode(str));
                    }else if(SysUtility.isNotEmpty(arr) && arr.length == 3){
                        uri = arr[0];
                        token = arr[1];
                        data = arr[2];
                        validateUriToken(uri, token);
                        getTopeConsumerService(uri).doCommand(data, header.getUserId());
                    }
                    break;
                case 2:
                    TopeRequestBodyProto2.RequestBody requestProto2Body = getRequest2Body(body);
                    uri = requestProto2Body.getUrl();
                    validateUriToken(uri, token);
                    whyMessageConsumerService = getTopeConsumerService(uri);
                    data = parseFrom(requestProto2Body.getData(), whyMessageConsumerService);
                    whyMessageConsumerService.doCommand(data, header.getUserId());
                    break;
                case 3:
                    TopeRequestBodyProto3.RequestBody requestProto3Body = getRequest3Body(body);
                    uri = requestProto3Body.getUri();
                    validateUriToken(uri, token);
                    whyMessageConsumerService = getTopeConsumerService(uri);
                    data = parseFrom(requestProto3Body.getData(), whyMessageConsumerService);
                    whyMessageConsumerService.doCommand(data, header.getUserId());
                    break;
                case 4:
                    TopeRequestBody topeRequestBody = (TopeRequestBody) SerializationUtils.deserialize(body);
                    uri = topeRequestBody.getUri();
                    token = topeRequestBody.getToken();
                    data = topeRequestBody.getData();

                    validateUriToken(uri, token);
                    getTopeConsumerService(uri).doCommand(data, header.getUserId());
                    break;
                case 5:
                    //com.alibaba.fastjson 1.2.31版本
                    JSONObject requestJson = (JSONObject) JSONObject.parse(new String(body, StandardCharsets.UTF_8));
                    uri = (String) requestJson.get("uri");
                    token = (String) requestJson.get("token");
                    data = requestJson.get("data");

                    validateUriToken(uri, token);
                    getTopeConsumerService(uri).doCommand(data, header.getUserId());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("业务执行错误，userId={},uri={}，token={}，data={}，异常=", userId, uri, token, data, e);
            throw new RuntimeException(e);
        }
    }

    private TopeRequestBodyProto2.RequestBody getRequest2Body(byte[] msg){
        TopeRequestBodyProto2.RequestBody requestProto2Body;
        try {
            requestProto2Body = TopeRequestBodyProto2.RequestBody.parseFrom(msg);
        } catch (Exception e) {
            throw new CommonException(CodecErrorCode.Err003.getCodeMassage(e));
        }
        return requestProto2Body;
    }

    private TopeRequestBodyProto3.RequestBody getRequest3Body(byte[] msg){
        TopeRequestBodyProto3.RequestBody requestProto3Body;
        try {
            requestProto3Body = TopeRequestBodyProto3.RequestBody.parseFrom(msg);
        } catch (Exception e) {
            throw new CommonException(CodecErrorCode.Err003.getCodeMassage(e));
        }
        return requestProto3Body;
    }

    /**
     * 格式化报文体
     * @param data
     * @param whyMessageConsumerService
     * @return
     */
    private Object parseFrom(Object data, WhyMessageConsumerService whyMessageConsumerService) {
        if (data == null) {
            return data;
        }
        //数据转换
        try {
            //proto 转换
            Class c = null;
            try {
                String className = whyMessageConsumerService.getClass().getGenericSuperclass().getTypeName();
                if("java.lang.Object".equals(className)){
                    className = whyMessageConsumerService.getClass().getTypeName();
                }
                ParameterizedType parameterizedType = (ParameterizedType) Class.forName(className).getGenericInterfaces()[0];
                c = (Class) parameterizedType.getActualTypeArguments()[0];
            } catch (CommonException e) {
                throw new CommonException(CodecErrorCode.Err001.getCodeMassage(e));
            }
            String str = ((ByteString)data).toStringUtf8();
            if(SysUtility.isNotEmpty(str) && str.startsWith("{")){
                Method newBuilder = c.getDeclaredMethod("newBuilder");
                Object o = newBuilder.invoke(null);
                try {
                    JsonFormat.merge(str, (Message.Builder) o);
                    data = ((Message.Builder) o).build();
                } catch (JsonFormat.ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Method method = c.getMethod("parseFrom", ByteString.class);
                method.setAccessible(true);
                data = method.invoke(c, data);
            }
        } catch (Exception e) {
            throw new CommonException(CodecErrorCode.Err010.getCodeMassage(e));
        }
        return data;
    }

    private void validateUriToken(String uri, String token){
        if(SysUtility.isEmpty(uri)){
            throw new CommonException(CodecErrorCode.Err006.getCodeMassage()+" ，uri参数不能为空");
        }

        //TODO
    }

    private WhyMessageConsumerService getTopeConsumerService(String uri){
        Object bean;
        try {
            bean = WhySpringUtils.getBean(uri);
        } catch (Exception e) {
            throw new CommonException(CodecErrorCode.Err007.getCodeMassage(e));
        }
        if (bean == null) {
            throw new CommonException(CodecErrorCode.Err008.getCodeMassage());
        }
        if (!(bean instanceof WhyMessageConsumerService)) {
            throw new CommonException(CodecErrorCode.Err009.getCodeMassage());
        }
        return (WhyMessageConsumerService) bean;
    }
}
