package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.Vehicle;
import com.project.CarGo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VehicleRepository vehicleRepository;

    public void sendRegistrationEmail(String recipient) {
        String subject = "CarGo Account Registration";
        String body = "Thank you for registering with CarGo!";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("cargorent25@gmail.com");

        mailSender.send(message);
    }

    public void sendAccountEmail(String recipient, boolean isAdmin) {
        String subject = "Information Related to CarGo Account";
        String body = "";

        if(isAdmin) {
            body = "Your account has been promoted to admin!";
        }
        else {
            body = "Your account has been changed from admin to user!";
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("cargorent25@gmail.com");

        mailSender.send(message);
    }

    public void sendReservationEmail(String recipient, Reservation reservation, boolean isModified) {

        Long id = reservation.getVehicleId();
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        String subject = "CarGo Reservation from " + reservation.getReservationStartDate() + " to " + reservation.getReservationEndDate();
        String line = "Thank you for reserving with CarGo! \n Here are your reservation details: \n ";

        if(isModified) {
            subject = "Modified CarGo Reservation from " + reservation.getReservationStartDate() + " to " + reservation.getReservationEndDate();
            line = "Thank you for reserving with CarGo! \n Here are your modified reservation details: \n ";
        }

        String body = line + "\nStart Date: " + reservation.getReservationStartDate() + "\nEnd Date: " + reservation.getReservationEndDate()
                + "\nVehicle: " + vehicle.getModel() + " " + vehicle.getMake()
                + "\nPayment Status: " + reservation.getPaymentStatus() + "\nTotal Price: " + reservation.getTotalPrice();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("cargorent25@gmail.com");

        mailSender.send(message);
    }

    public void sendCancelReservationEmail(String recipient, Reservation reservation) {

        Long id = reservation.getVehicleId();
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        String subject = "CANCELLED-CarGo Reservation from " + reservation.getReservationStartDate() + " to " + reservation.getReservationEndDate();
        String body = "Your reservation has been cancelled. \n Here are your cancelled reservation details: \n "
                + "\nStart Date: " + reservation.getReservationStartDate() + "\nEnd Date: " + reservation.getReservationEndDate()
                + "\nVehicle: " + vehicle.getModel() + " " + vehicle.getMake();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("cargorent25@gmail.com");

        mailSender.send(message);
    }
}