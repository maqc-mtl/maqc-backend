package com.maqc.backend.dto;

import com.maqc.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String email;
    private User.Role role;
    private User.PlanType planType;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public User.PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(User.PlanType planType) {
        this.planType = planType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Manual Builder
    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    public static class AuthenticationResponseBuilder {
        private String email;
        private User.Role role;
        private User.PlanType planType;
        private String phoneNumber;
        private String firstName;
        private String lastName;

        public AuthenticationResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthenticationResponseBuilder role(User.Role role) {
            this.role = role;
            return this;
        }

        public AuthenticationResponseBuilder planType(User.PlanType planType) {
            this.planType = planType;
            return this;
        }

        public AuthenticationResponseBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public AuthenticationResponseBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public AuthenticationResponseBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public AuthenticationResponse build() {
            AuthenticationResponse res = new AuthenticationResponse();
            res.setEmail(email);
            res.setRole(role);
            res.setPlanType(planType);
            res.setPhoneNumber(phoneNumber);
            res.setFirstName(firstName);
            res.setLastName(lastName);
            return res;
        }
    }
}
