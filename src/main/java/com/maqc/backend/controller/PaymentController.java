package com.maqc.backend.controller;

import com.maqc.backend.dto.PaymentRequest;
import com.maqc.backend.model.User;
import com.maqc.backend.repository.UserRepository;
import com.maqc.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequest request) {
        // In a real application, you would integrate with a payment gateway like
        // Stripe, PayPal, etc.
        // For this demo, we'll simulate a successful payment

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        // Update user's plan type
        User.PlanType planType;
        try {
            planType = User.PlanType.valueOf(request.getPlanType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid plan type: " + request.getPlanType());
        }

        user.setPlanType(planType);
        userRepository.save(user);
        // phrase3
        // // Send receipt email
        // try {
        // emailService.sendReceiptEmail(user, request.getPlanType());
        // } catch (Exception e) {
        // // Log error but don't fail the payment
        // System.err.println("Failed to send receipt email: " + e.getMessage());
        // }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment processed successfully");
        response.put("planType", planType);
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }
}