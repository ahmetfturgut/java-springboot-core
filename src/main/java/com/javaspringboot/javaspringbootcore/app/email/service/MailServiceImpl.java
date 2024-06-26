package com.javaspringboot.javaspringbootcore.app.email.service;


import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import com.javaspringboot.javaspringbootcore.app.user.service.UserService;
import com.javaspringboot.javaspringbootcore.core.exception.ApiError;
import com.javaspringboot.javaspringbootcore.core.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);


    public void sendSignUpEmail(User user, String code) {
        try {
            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("code", code);

            String process = templateEngine.process("emails/singUp", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setSubject("Welcome " + user.getUsername());
            helper.setText(process, true);
            helper.setTo(user.getEmail());

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Failed to send signup email to: {}, with verification code: {}. Error: {}", user.getEmail(), code, e.getMessage());
            throw new ApiException(ApiError.NOT_AUTHORIZED);
        }

    }


}
