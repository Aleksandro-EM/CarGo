package com.project.CarGo.controller;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.Map;

@Controller
@RequestMapping
public class PaymentController {

    private final ReservationRepository reservationRepository;
    private final StripeService stripeService;

    public PaymentController(ReservationRepository reservationRepository, StripeService stripeService) {
        this.reservationRepository = reservationRepository;
        this.stripeService = stripeService;
    }

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @GetMapping("/checkout/{reservationId}")
    public String checkoutPage(@PathVariable Long reservationId, Model model) {
        Reservation r = reservationRepository.findById(reservationId).orElseThrow();
        model.addAttribute("reservation", r);
        model.addAttribute("publishableKey", publishableKey);
        return "checkout";
    }

    @PostMapping("/api/payments/create-intent")
    @ResponseBody
    public ResponseEntity<Map<String,String>> createIntent(@RequestParam Long reservationId) throws com.stripe.exception.StripeException {
        var r = reservationRepository.findById(reservationId).orElseThrow();
        com.stripe.model.PaymentIntent pi;
        if (r.getStripePaymentId() != null && !r.getStripePaymentId().isBlank()) {
            pi = stripeService.retrieve(r.getStripePaymentId());
            if ("succeeded".equals(pi.getStatus())) return ResponseEntity.ok(Map.of("alreadyPaid","true"));
            if ("canceled".equals(pi.getStatus())) {
                pi = stripeService.createPaymentIntentForReservation(r);
                r.setStripePaymentId(pi.getId());
                reservationRepository.save(r);
            }
        } else {
            pi = stripeService.createPaymentIntentForReservation(r);
            r.setStripePaymentId(pi.getId());
            reservationRepository.save(r);
        }
        return ResponseEntity.ok(Map.of("clientSecret", pi.getClientSecret(), "publishableKey", publishableKey));
    }
}