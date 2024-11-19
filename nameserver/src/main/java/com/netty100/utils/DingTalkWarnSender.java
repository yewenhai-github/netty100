package com.netty100.utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.netty100.config.DingTalkConfig;
import com.netty100.entity.User;
import com.netty100.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(value = DingTalkConfig.class)
public class DingTalkWarnSender implements WarnSender {

    private final UserService userService;

    private final DingTalkConfig dingTalkConfig;

    private final ThreadPoolTaskExecutor executor;

    private final Environment environment;

    public String formatUrl() {
        try {
            Long timestamp = System.currentTimeMillis();
            String secret = dingTalkConfig.getSecret();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            return "https://oapi.dingtalk.com/robot/send?access_token=" + dingTalkConfig.getToken() +
                    "&timestamp=" + timestamp + "&sign=" + sign;
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            log.error("构建钉钉发送url失败", e);
            return null;
        }
    }

    @Override
    public void sendWarn(List<Integer> userIds, String title, String content) {
        executor.execute(() -> {
            List<String> mobiles = new ArrayList<>();
            if (!userIds.isEmpty()) {
                mobiles.addAll(userService.getByIds(userIds).stream().map(User::getDingTalk).filter(StringUtils::hasText).collect(Collectors.toList()));
            }
            String url = formatUrl();
            if (Objects.isNull(url)) {
                return;
            }
            DingTalkClient client = new DefaultDingTalkClient(url);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(buildWarnContent(title, content, mobiles));
            request.setText(text);
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            if (!mobiles.isEmpty()) {
                at.setAtMobiles(mobiles);
            }
            request.setAt(at);
            try {
                OapiRobotSendResponse execute = client.execute(request);
                log.info(execute.isSuccess() ? "钉钉告警发送成功" : "钉钉告警发送失败");
            } catch (ApiException e) {
                log.error("钉钉告警发送出现异常", e);
            }
        });
    }

    private String buildWarnContent(String title, String content, List<String> notifyList) {
        String profiles = Optional.ofNullable(environment.getActiveProfiles()).map(arr -> String.join(",", arr)).orElseGet(() -> "-");
        StringBuilder sb = new StringBuilder("NettyCloud告警\n");
        sb.append("环境：").append(profiles).append("\n");
        sb.append("时间：").append(DateUtils.getFormatNow(DateUtils.YMD_DASH_BLANK_HMS_COLON)).append("\n");
        sb.append("标题：").append(title).append("\n");
        sb.append("告警内容: ").append(content);
        for (String mb : notifyList) {
            sb.append('@').append(mb);
        }
        return sb.toString();
    }
}
