package com.project.CarGo.controller;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentRetrieveParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentConfirmationController {

    public PaymentConfirmationController(@Value("${stripe.secret-key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    @GetMapping("/payment/confirm")
    public String confirm(
            @RequestParam(name = "payment_intent", required = false) String paymentIntentId,
            @RequestParam(name = "redirect_status", required = false) String redirectStatus,
            @RequestParam(name = "reservationId", required = false) Long reservationId,
            Model model
    ) throws Exception {

        boolean success = false;
        String amountText = null;
        String currency = null;
        String chargeId = null;

        if (paymentIntentId != null) {
            PaymentIntentRetrieveParams params = PaymentIntentRetrieveParams.builder()
                    .addExpand("latest_charge")
                    .build();
            PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId, params, null);

            String status = pi.getStatus();
            success = "succeeded".equalsIgnoreCase(status);

            if (pi.getAmount() != null && pi.getCurrency() != null) {
                currency = pi.getCurrency().toUpperCase();
                long amt = pi.getAmount(); // in smallest currency unit
                amountText = String.format("%.2f", amt / 100.0);
            }

            if (pi.getLatestChargeObject() != null) {
                chargeId = pi.getLatestChargeObject().getId();
            }

            model.addAttribute("paymentIntentId", pi.getId());
            model.addAttribute("status", status);
        }

        model.addAttribute("success", success);
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("amount", amountText);
        model.addAttribute("currency", currency);
        model.addAttribute("chargeId", chargeId);

        return "payment-confirmation";
    }
}