package com.maqc.backend.controller;

import com.maqc.backend.dto.PaymentRequest;
import com.maqc.backend.model.User;
import com.maqc.backend.repository.UserRepository;
import com.maqc.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import com.maqc.backend.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final StripeService stripeService;
    private final com.maqc.backend.repository.TransactionRepository transactionRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, String> request) {
        try {
            String planType = request.get("planType");
            String email = request.get("email");

            com.stripe.model.PaymentIntent intent = stripeService.createPaymentIntent(planType, email);

            // Create and save a new Transaction record
            User user = userRepository.findByEmail(email.toLowerCase().trim())
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));

            com.maqc.backend.model.Transaction transaction = new com.maqc.backend.model.Transaction();
            transaction.setUser(user);
            transaction.setAmount(intent.getAmount() / 100.0); // Convert cents to dollars
            transaction.setCurrency(intent.getCurrency());
            transaction.setStripePaymentIntentId(intent.getId());
            transaction.setPlanType(planType.toUpperCase());
            transaction.setStatus(intent.getStatus());
            transaction.setCreatedAt(java.time.LocalDateTime.now());

            transaction = transactionRepository.save(transaction);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            System.out.println("contentId" + intent.getId());
            response.put("transactionId", transaction.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/transaction/{id}/status")
    public ResponseEntity<Map<String, Object>> getTransactionStatus(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(t -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", t.getStatus());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            com.stripe.model.Event event = com.stripe.net.Webhook.constructEvent(payload, sigHeader, webhookSecret);

            com.stripe.model.EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            com.stripe.model.StripeObject stripeObject;
            if (deserializer.getObject().isPresent()) {
                // SDK version matches — happy path
                stripeObject = deserializer.getObject().get();
            } else {
                // SDK/API version mismatch — deserialize anyway (unsafe but functional)
                stripeObject = deserializer.deserializeUnsafe();
            }

            if ("payment_intent.succeeded".equals(event.getType())) {
                com.stripe.model.PaymentIntent intent = (com.stripe.model.PaymentIntent) stripeObject;

                List<com.maqc.backend.model.Transaction> transactions = transactionRepository
                        .findByStripePaymentIntentId(intent.getId());
                if (!transactions.isEmpty()) {
                    com.maqc.backend.model.Transaction t = transactions.get(0);
                    t.setStatus("SUCCESS");
                    transactionRepository.save(t);
                }

                String email = intent.getMetadata().get("userEmail");
                String planType = intent.getMetadata().get("planType");
                updateUserPlan(email, planType);

            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                com.stripe.model.PaymentIntent intent = (com.stripe.model.PaymentIntent) stripeObject;

                List<com.maqc.backend.model.Transaction> transactions = transactionRepository
                        .findByStripePaymentIntentId(intent.getId());
                if (!transactions.isEmpty()) {
                    com.maqc.backend.model.Transaction t = transactions.get(0);
                    t.setStatus("FAILED");
                    transactionRepository.save(t);
                }
            }

            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

    private void updateUserPlan(String email, String planTypeStr) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        User.PlanType planType = User.PlanType.valueOf(planTypeStr.toUpperCase());
        user.setPlanType(planType);
        userRepository.save(user);

        try {
            emailService.sendReceiptEmail(user, planTypeStr);
        } catch (Exception e) {
            System.err.println("Failed to send receipt email: " + e.getMessage());
        }
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequest request) {
        updateUserPlan(request.getEmail(), request.getPlanType());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment processed successfully");

        return ResponseEntity.ok(response);
    }
}