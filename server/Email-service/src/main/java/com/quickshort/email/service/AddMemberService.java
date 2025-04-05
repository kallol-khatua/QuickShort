package com.quickshort.email.service;

import com.quickshort.common.enums.MemberType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class AddMemberService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.add.baseurl}")
    private String baseUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMemberService.class);

    @Async
    public void sendEmail(String to, MemberType memberType, String workspaceId) throws MessagingException, UnsupportedEncodingException {
        LOGGER.info(String.format("Sending invitation email to -> %s", to));

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("subject");
        String url;
        if (memberType == MemberType.MEMBER) {
            url = baseUrl + "/" + workspaceId + "/" + "join-as-member";
        } else {
            url = baseUrl + "/" + workspaceId + "/" + "join-as-owner";
        }
        helper.setText(url, true);

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
