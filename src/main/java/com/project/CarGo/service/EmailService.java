package com.project.CarGo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String recipient) throws MessagingException {
        String subject = "CarGo Account Registration";
        String body = "<p>Thank you for registering with CarGo!</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom("@gmail.com");

        mailSender.send(message);
    }
}