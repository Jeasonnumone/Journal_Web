package cn.deru.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerifyCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("期刊系统注册验证码");
        message.setText("您的注册验证码是：" + code + "，有效期 5 分钟。请勿泄露给他人。");

        try {
            mailSender.send(message);
            log.info("验证码邮件已发送至：{}", to);
        } catch (MailException e) {
            log.error("发送邮箱验证码失败,邮箱：{}",to);
            throw new RuntimeException(e);
        }
    }
}
