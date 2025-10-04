package com.project.CarGo.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.CarGo.entity.ReservationStatus;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private final ReservationRepository reservationRepository;
    private final StripeService stripeService;

    public StripeWebhookController(ReservationRepository reservationRepository, StripeService stripeService) {
        this.reservationRepository = reservationRepository;
        this.stripeService = stripeService;
    }

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<String> handle(@RequestHeader(value = "Stripe-Signature", required = false) String sigHeader,
                                         @RequestBody String payload) {
        Event event;
        try {
            if (webhookSecret != null && !webhookSecret.isBlank() && sigHeader != null) {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } else {
                event = Event.GSON.fromJson(payload, Event.class);
            }
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        String type = event.getType();

        if (type.startsWith("payment_intent.")) {
            PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (pi == null) {
                JsonObject o = JsonParser.parseString(payload).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("object");
                String id = o.has("id") ? o.get("id").getAsString() : null;
                if (id != null) {
                    try { pi = stripeService.retrieve(id); } catch (StripeException ignored) {}
                }
            }
            if (pi != null) upsert(pi, toPs(type));
        } else if (type.startsWith("charge.")) {
            Charge ch = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
            String piId = ch != null ? ch.getPaymentIntent() : null;
            if (piId == null) {
                JsonObject o = JsonParser.parseString(payload).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("object");
                if (o.has("payment_intent")) piId = o.get("payment_intent").getAsString();
            }
            if (piId != null) {
                try {
                    PaymentIntent pi = stripeService.retrieve(piId);
                    upsert(pi, toPs(type));
                } catch (StripeException ignored) {}
            }
        }

        return ResponseEntity.ok("ok");
    }

    private void upsert(PaymentIntent pi, String ps) {
        if (ps == null) return;
        String piId = pi.getId();
        var r = reservationRepository.findByStripePaymentId(piId)
                .orElseGet(() -> {
                    String rid = pi.getMetadata() != null ? pi.getMetadata().get("reservationId") : null;
                    if (rid == null) return null;
                    try { return reservationRepository.findById(Long.parseLong(rid)).orElse(null); }
                    catch (NumberFormatException e) { return null; }
                });
        if (r == null) return;
        r.setStripePaymentId(piId);
        r.setPaymentStatus(ps);
        if ("succeeded".equals(ps)) {
            r.setStatus(ReservationStatus.PAID);
            r.setHoldExpiresAt(null);
        } else if ("canceled".equals(ps)) {
            r.setStatus(ReservationStatus.CANCELLED);
        }
        reservationRepository.save(r);
    }

    private String toPs(String type) {
        if ("payment_intent.succeeded".equals(type) || "charge.succeeded".equals(type)) return "succeeded";
        if ("payment_intent.canceled".equals(type) || "payment_intent.payment_failed".equals(type) || "charge.failed".equals(type)) return "canceled";
        return null;
    }
}