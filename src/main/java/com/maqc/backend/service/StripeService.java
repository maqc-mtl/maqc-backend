package com.maqc.backend.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeService {

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    private static final double GST_RATE = 0.05;
    private static final double QST_RATE = 0.09975;

    public PaymentIntent createPaymentIntent(String planType, String userEmail) throws Exception {
        long amount = calculateAmountInCents(planType);
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("cad")
                .setReceiptEmail(userEmail)
                .putMetadata("planType", planType.toUpperCase())
                .putMetadata("userEmail", userEmail)
                .build();

        return PaymentIntent.create(params);
    }

    private long calculateAmountInCents(String planType) {
        double basePrice;
        switch (planType.toLowerCase()) {
            case "basic": basePrice = 99.0; break;
            case "plus": basePrice = 199.0; break;
            case "pro": basePrice = 399.0; break;
            default: basePrice = 0.0;
        }

        if (basePrice == 0) return 0;

        double gst = Math.round(basePrice * GST_RATE * 100.0) / 100.0;
        double qst = Math.round(basePrice * QST_RATE * 100.0) / 100.0;
        double total = basePrice + gst + qst;

        return Math.round(total * 100.0);
    }
}
