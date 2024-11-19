package com.netty100.utils;

import com.netty100.entity.User;
import com.netty100.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailWarnSender implements WarnSender {

    private final UserService userService;

    private final ThreadPoolTaskExecutor executor;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendWarn(List<Integer> userIds, String title, String content) {
        executor.execute(() -> {
            try {
                val emails = new ArrayList<String>();
                if (!userIds.isEmpty()) {
                    emails.addAll(userService.getByIds(userIds).stream().map(User::getEmail).filter(StringUtils::hasText).collect(Collectors.toList()));
                }
                for (String email : emails) {
                    MimeMessage message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setFrom("topejob-qa@ienglish.cn");
                    helper.setTo(email);
                    helper.setSubject(title);
                    helper.setText(content, true);
                    javaMailSender.send(message);
                    log.info("邮件发送成功,邮件接收地址:{}", email);
                }
            } catch (MessagingException e) {
                log.error("邮件发送失败", e);
            }
        });
    }
}
