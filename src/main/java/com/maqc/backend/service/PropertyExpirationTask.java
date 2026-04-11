package com.maqc.backend.service;

import com.maqc.backend.model.Property;
import com.maqc.backend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PropertyExpirationTask {

    private final PropertyRepository propertyRepository;

    // Run every hour to check for expired properties
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredProperties() {
        log.info("Running property expiration check...");
        LocalDateTime now = LocalDateTime.now();

        List<Property> expiredProperties = propertyRepository.findByStatusAndExpirationDateBefore(
                Property.PropertyStatus.APPROVED, now);

        for (Property property : expiredProperties) {
            log.info("Expiring property ID: {} due to plan expiration", property.getId());
            property.setStatus(Property.PropertyStatus.EXPIRED);
            propertyRepository.save(property);
        }

        log.info("Property expiration check completed. {} properties expired.", expiredProperties.size());
    }
}
