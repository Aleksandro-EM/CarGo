package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.ReservationStatus;
import com.project.CarGo.repository.ReservationRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    private final ReservationRepository reservationRepository;

    public PaymentIntent createPaymentIntentForReservation(Reservation r) throws StripeException {
        Stripe.apiKey = secretKey;

        r.setStatus(ReservationStatus.PENDING);
        r.setHoldExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000L)); //5min
        reservationRepository.save(r);

        BigDecimal amount = BigDecimal.valueOf(r.getTotalPrice())
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.longValueExact())
                .setCurrency("cad")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .putMetadata("reservationId", String.valueOf(r.getId()))
                .putMetadata("vehicleId", r.getVehicle().getId() == null ? "" : String.valueOf(r.getVehicle().getId()))
                .putMetadata("userEmail", r.getUser() == null ? "" : Objects.toString(r.getUser().getEmail(), ""))
                .build();

        PaymentIntent pi = PaymentIntent.create(params);
        r.setStripePaymentId(pi.getId());
        reservationRepository.save(r);
        return pi;
    }

    public PaymentIntent retrieve(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;
        return PaymentIntent.retrieve(paymentIntentId);
    }

    public void cancelPaymentIntentSilently(String paymentIntentId) {
        Stripe.apiKey = secretKey;
        try {
            PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
            pi.cancel();
        } catch (Exception ignored) { }
    }
}