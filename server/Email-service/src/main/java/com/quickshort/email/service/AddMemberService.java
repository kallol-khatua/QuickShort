package com.quickshort.email.service;

import com.quickshort.common.enums.MemberType;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Year;

@Service
public class AddMemberService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.add.baseurl}")
    private String baseUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMemberService.class);

    @Async
    public void sendEmail(String to, MemberType memberType, String workspaceId) throws MessagingException, IOException {
        LOGGER.info(String.format("Sending invitation email to -> %s", to));

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String url;
        if (memberType == MemberType.MEMBER) {
            url = baseUrl + "/" + workspaceId + "/" + "join-as-member";
        } else {
            url = baseUrl + "/" + workspaceId + "/" + "join-as-owner";
        }

        String htmlContent = new String(
                new ClassPathResource("templates/join-workspace.html").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        htmlContent = htmlContent
                .replace("${url}", url)
                .replace("${year}", String.valueOf(Year.now().getValue()));

        helper.setTo(to);
        helper.setSubject("Join workspace on QuickShort");
        helper.setText(htmlContent, true);

        helper.setFrom(new InternetAddress("service@rideassure.in", "QuickShort"));
        helper.setReplyTo("service@rideassure.in");

        try {
            javaMailSender.send(mimeMessage);
            LOGGER.info(String.format("invitation email sent successfully to -> %s", to));
        } catch (Exception e) {
            LOGGER.error("Error processing mail: {}", e.getMessage(), e);
        }
    }
}
