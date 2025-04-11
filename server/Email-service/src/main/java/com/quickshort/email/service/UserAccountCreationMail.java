package com.quickshort.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.UUID;

@Service
public class UserAccountCreationMail {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.add.baseurl}")
    private String baseUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountCreationMail.class);

    @Async
    public void sendEmail(String to, String subject, UUID id) throws MessagingException, IOException {
        LOGGER.info(String.format("Sending account creation mail to -> %s", to));

//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        message.setFrom("Kallol<kallol.khatua@rideassure.in");

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String htmlContent = new String(
                new ClassPathResource("templates/verify-email.html").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        htmlContent = htmlContent
                .replace("${verificationLink}", baseUrl + "/verify-account?id=" + id)
                .replace("${year}", String.valueOf(Year.now().getValue()));

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Set a visible sender email
        helper.setFrom(new InternetAddress("service@rideassure.in", "QuickShort"));
        helper.setReplyTo("service@rideassure.in");

        try {
//            javaMailSender.send(message);
            javaMailSender.send(mimeMessage);
            LOGGER.info(String.format("Account creation mail sent successfully to -> %s", to));
        } catch (Exception e) {
            LOGGER.error("Error processing mail: {}", e.getMessage(), e);
        }
    }
}
