package com.maqc.backend.service;

import com.maqc.backend.dto.AuthenticationRequest;
import com.maqc.backend.dto.AuthenticationResponse;
import com.maqc.backend.dto.RegisterRequest;
import com.maqc.backend.model.User;
import com.maqc.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository repository;

        public AuthenticationResponse register(RegisterRequest request) {
                // Check if email already exists
                Optional<User> existingUser = repository.findByEmail(request.getEmail());
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
                Optional<User> userOpt = repository.findByEmail(request.getEmail());

                if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                                return AuthenticationResponse.builder()
                                                .email(user.getEmail())
                                                .role(user.getRole())
                                                .planType(user.getPlanType())
                                                .phoneNumber(user.getPhoneNumber())
                                                .firstName(user.getFirstName())
                                                .lastName(user.getLastName())
                                                .build();
                        }
                }

                throw new RuntimeException("Invalid credentials");
        }
}
