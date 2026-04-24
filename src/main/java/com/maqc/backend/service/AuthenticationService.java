package com.maqc.backend.service;

import com.maqc.backend.dto.AuthenticationRequest;
import com.maqc.backend.dto.AuthenticationResponse;
import com.maqc.backend.dto.RegisterRequest;
import com.maqc.backend.exception.AdminActionForbiddenException;
import com.maqc.backend.exception.ExpiredResetTokenException;
import com.maqc.backend.exception.InvalidCredentialsException;
import com.maqc.backend.exception.InvalidResetTokenException;
import com.maqc.backend.model.User;
import com.maqc.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository repository;
        private final EmailService emailService;
        private final BrevoEmailService brevoEmailService;

        public AuthenticationResponse register(RegisterRequest request) {
                String email = request.getEmail().toLowerCase().trim();
                // Check if email already exists
                Optional<User> existingUser = repository.findByEmail(email);
                if (existingUser.isPresent()) {
                        throw new RuntimeException("Email is already registered");
                }

                String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

                // Set planType from request, default to FREE if not provided
                User.PlanType planType = request.getPlanType() != null ? request.getPlanType() : User.PlanType.FREE;

                // Always set role to USER for now (agent functionality can be added later)
                User user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .email(request.getEmail())
                                .password(hashedPassword)
                                .role(User.Role.USER)
                                .phoneNumber(request.getPhoneNumber())
                                .planType(planType)
                                .build();

                repository.save(user);

                return AuthenticationResponse.builder()
                                .email(user.getEmail())
                                .role(user.getRole())
                                .planType(user.getPlanType())
                                .phoneNumber(user.getPhoneNumber())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                String email = request.getEmail().toLowerCase().trim();
                Optional<User> userOpt = repository.findByEmail(email);

                if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                                return AuthenticationResponse.builder()
                                                .id(user.getId())
                                                .email(user.getEmail())
                                                .role(user.getRole())
                                                .planType(user.getPlanType())
                                                .phoneNumber(user.getPhoneNumber())
                                                .firstName(user.getFirstName())
                                                .lastName(user.getLastName())
                                                .build();
                        }
                }

                throw new InvalidCredentialsException("Invalid credentials", "INVALID_CREDENTIALS");
        }

        public void forgotPassword(String email) {
                String normalizedEmail = email.toLowerCase().trim();
                Optional<User> userOpt = repository.findByEmail(normalizedEmail);

                if (userOpt.isPresent()) {
                        User user = userOpt.get();

                        if (user.getRole() == User.Role.ADMIN) {
                                throw new AdminActionForbiddenException("Administrators cannot use self-service password reset. Please contact system owner.", "ADMIN_RESET_FORBIDDEN");
                        }

                        // Generate reset token
                        String resetToken = UUID.randomUUID().toString();
                        user.setResetToken(resetToken);
                        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

                        repository.save(user);

                        // Send email with reset link
                        try {
                                brevoEmailService.sendPasswordResetEmail(user);
                        } catch (Exception e) {
                                throw new RuntimeException("Failed to send password reset email");
                        }
                } else {
                        // For security, don't reveal that email doesn't exist
                        // Just log and return success to prevent email enumeration
                        System.out.println("Password reset requested for non-existent email: " + normalizedEmail);
                }
        }

        public void resetPassword(String token, String newPassword) {
                Optional<User> userOpt = repository.findByResetToken(token);

                if (userOpt.isEmpty()) {
                        throw new InvalidResetTokenException("Invalid or expired reset token", "INVALID_RESET_TOKEN");
                }

                User user = userOpt.get();

                // Check if token is expired
                if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                        user.setResetToken(null);
                        user.setResetTokenExpiry(null);
                        repository.save(user);
                        throw new ExpiredResetTokenException("Reset token has expired", "EXPIRED_RESET_TOKEN");
                }

                // Update password
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                user.setPassword(hashedPassword);

                // Clear reset token
                user.setResetToken(null);
                user.setResetTokenExpiry(null);

                repository.save(user);
        }
}
