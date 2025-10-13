package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.Vehicle;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final String FROM_EMAIL = "cargorent25@gmail.com";

    public void sendRegistrationEmail(String recipient) {
        String subject = "CarGo Account Registration";
        String body = "Thank you for registering with CarGo!";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(FROM_EMAIL);

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
        message.setFrom(FROM_EMAIL);

        mailSender.send(message);
    }

    public void sendReservationEmail(String recipient, Reservation reservation, boolean isModified) {

        Long id = reservation.getVehicle().getId();
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(reservation.getReservationStartDate());
        String end = sdf.format(reservation.getReservationEndDate());

        String subject = "CarGo Reservation from " + start + " to " + end;
        String line = "Thank you for reserving with CarGo! \n\nHere are your reservation details: \n";

        if(isModified) {
            subject = "Modified CarGo Reservation from " + start + " to " + end;
            line = "Thank you for reserving with CarGo! \n\nHere are your modified reservation details: \n";
        }

        String body = line + "Start Date: " + start + "\nEnd Date: " + end
                + "\nVehicle: " + vehicle.getModel() + " " + vehicle.getMake()
                + "\n\nReservation Status: " + reservation.getStatus() + "\nTotal Price: " + String.format("%.2f", reservation.getTotalPrice());

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(FROM_EMAIL);

        mailSender.send(message);
    }

    public void sendReservationEmailWithDelay(String recipient, Reservation reservation) {
        scheduler.schedule(() -> {
            reservationRepository.findById(reservation.getId()).ifPresent(newReservation -> {
                sendReservationEmail(recipient, newReservation, false);
            });
        }, 30, TimeUnit.SECONDS);
    }

    public void sendCancelReservationEmail(String recipient, Reservation reservation) {

        Long id = reservation.getVehicle().getId();
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + id));

        String subject = "CANCELLED-CarGo Reservation from " + reservation.getReservationStartDate() + " to " + reservation.getReservationEndDate();
        String body = "Your reservation has been cancelled. \n\nHere are your cancelled reservation details: \n "
                + "Start Date: " + reservation.getReservationStartDate() + "\nEnd Date: " + reservation.getReservationEndDate()
                + "\nVehicle: " + vehicle.getModel() + " " + vehicle.getMake();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(FROM_EMAIL);

        mailSender.send(message);
    }
}