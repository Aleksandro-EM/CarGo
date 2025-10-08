package com.project.CarGo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String recipient) {
        String subject = "CarGo Account Registration";
        String body = "Thank you for registering with CarGo!";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("cargorent25@gmail.com");

        mailSender.send(message);
    }
}