package com.maqc.backend.config;

import com.maqc.backend.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class PlanLimits {

    public static class Limits {
        public final int maxRentalListings;
        public final int maxSaleListings;
        public final int maxImagesPerListing;
        public final int displayDays;

        public Limits(int maxRentalListings, int maxSaleListings, int maxImagesPerListing, int displayDays) {
            this.maxRentalListings = maxRentalListings;
            this.maxSaleListings = maxSaleListings;
            this.maxImagesPerListing = maxImagesPerListing;
            this.displayDays = displayDays;
        }
    }

    public Limits getLimitsForPlan(User.PlanType planType) {
        switch (planType) {
            case FREE:
                return new Limits(2, 1, 2, 7);
            case BASIC:
                return new Limits(3, 2, 5, 30);
            case PLUS:
                return new Limits(6, 4, 10, 30);
            case PRO:
                return new Limits(12, 10, 10, 30);
            default:
                return new Limits(0, 0, 0, 0);
        }
    }

    public LocalDateTime calculateExpirationDate(User.PlanType planType) {
        int displayDays = getLimitsForPlan(planType).displayDays;
        return LocalDateTime.now().plusDays(displayDays);
    }
}
