package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    public PaymentIntent createPaymentIntentForReservation(Reservation r) throws StripeException {
        Stripe.apiKey = secretKey;
        BigDecimal amount = BigDecimal.valueOf(r.getTotalPrice()).movePointRight(2).setScale(0, RoundingMode.HALF_UP);
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.longValueExact())
                .setCurrency("cad")
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .putMetadata("reservationId", String.valueOf(r.getId()))
                .putMetadata("vehicleId", r.getVehicleId() == null ? "" : String.valueOf(r.getVehicleId()))
                .putMetadata("userEmail", r.getUser() == null ? "" : Objects.toString(r.getUser().getEmail(), ""))
                .build();
        return PaymentIntent.create(params);
    }

    public PaymentIntent retrieve(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;
        return PaymentIntent.retrieve(paymentIntentId);
    }
}