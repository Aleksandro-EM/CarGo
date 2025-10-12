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
    public ResponseEntity<String> handle(
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader,
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

        try {
            if (type.startsWith("payment_intent.")) {
                PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (pi == null) {
                    JsonObject o = JsonParser.parseString(payload)
                            .getAsJsonObject().getAsJsonObject("data").getAsJsonObject("object");
                    String id = o.has("id") ? o.get("id").getAsString() : null;
                    if (id != null) {
                        pi = stripeService.retrieve(id);
                    }
                }
                applyLatest(pi);
            } else if (type.startsWith("charge.")) {
                Charge ch = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
                String piId = ch != null ? ch.getPaymentIntent() : null;
                if (piId == null) {
                    JsonObject o = JsonParser.parseString(payload)
                            .getAsJsonObject().getAsJsonObject("data").getAsJsonObject("object");
                    if (o.has("payment_intent")) piId = o.get("payment_intent").getAsString();
                }
                if (piId != null) {
                    PaymentIntent pi = stripeService.retrieve(piId);
                    applyLatest(pi);
                }
            }
        } catch (StripeException ignored) {
        }

        return ResponseEntity.ok("ok");
    }

    private void applyLatest(PaymentIntent pi) {
        if (pi == null) return;

        String piStatus = pi.getStatus();
        String paymentStatus = switch (piStatus) {
            case "succeeded" -> "succeeded";
            case "canceled", "requires_payment_method" -> "canceled";
            default -> "hold";
        };

        String piId = pi.getId();
        var r = reservationRepository.findByStripePaymentId(piId)
                .orElseGet(() -> {
                    var meta = pi.getMetadata();
                    if (meta == null) return null;
                    String rid = meta.get("reservationId");
                    if (rid == null) return null;
                    try {
                        return reservationRepository.findById(Long.parseLong(rid)).orElse(null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                });
        if (r == null) return;

        //guard for late payments
        if (r.getStatus() == ReservationStatus.CANCELLED && "succeeded".equals(paymentStatus)) {
            return;
        }

        boolean isTerminalPaid = r.getStatus() == ReservationStatus.PAID;
        boolean isTerminalCanceled = r.getStatus() == ReservationStatus.CANCELLED;

        if (("succeeded".equals(paymentStatus) && isTerminalPaid)
                || ("canceled".equals(paymentStatus) && isTerminalCanceled)) {
            return;
        }

        r.setStripePaymentId(piId);
        r.setPaymentStatus(paymentStatus);

        if ("succeeded".equals(paymentStatus)) {
            r.setStatus(ReservationStatus.PAID);
            r.setHoldExpiresAt(null);
        } else if ("canceled".equals(paymentStatus)) {
            r.setStatus(ReservationStatus.CANCELLED);
            r.setHoldExpiresAt(null);
        } else {
            if (r.getStatus() == null) {
                r.setStatus(ReservationStatus.PENDING);
            }
        }

        reservationRepository.save(r);
    }
}
