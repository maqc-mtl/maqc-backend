package com.maqc.backend.controller;

import com.maqc.backend.dto.ContactRequest;
import com.maqc.backend.dto.PropertyScoreDTO;
import com.maqc.backend.model.Property;
import com.maqc.backend.service.BrevoEmailService;
import com.maqc.backend.service.EmailService;
import com.maqc.backend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyController {

    private final PropertyService service;
    private final EmailService emailService;
    private final BrevoEmailService brevoEmailService;

    @GetMapping("/public/search")
    public ResponseEntity<Page<Property>> searchProperties(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "type", required = false) Property.PropertyType type,
            @RequestParam(value = "businessType", required = false) Property.BusinessType businessType,
            @RequestParam(value = "listingType", required = false) Property.ListingType listingType,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minBedrooms", required = false) Integer minBedrooms,
            @RequestParam(value = "minBathrooms", required = false) Integer minBathrooms,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.searchProperties(
                keyword,
                area,
                type,
                businessType,
                listingType,
                minPrice,
                maxPrice,
                minBedrooms,
                minBathrooms,
                Property.PropertyStatus.APPROVED,
                pageable));

    }

    @GetMapping("/public")
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(service.getAllProperties());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Property> getPropertyById(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return service.getPropertyById(id)
                .map(p -> {
                    service.checkFavoriteStatus(p, userId);
                    return ResponseEntity.ok(p);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Property> createProperty(
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Support LocalDateTime
        Property property = mapper.readValue(propertyJson, Property.class);

        return ResponseEntity.ok(service.createProperty(property, files));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable("id") Long id, @RequestBody Property property) {
        return ResponseEntity.ok(service.updateProperty(id, property));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable("id") Long id) {
        service.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/{propertyId}/favorite")
    public ResponseEntity<Property> toggleFavorite(
            @PathVariable("propertyId") Long propertyId,
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(service.toggleFavorite(propertyId, userId));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Property> incrementViewCount(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.incrementViewCount(id));
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Page<Property>> getFavoriteProperties(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.getFavoriteProperties(userId, pageable));
    }

    @PostMapping("/{id}/contact")
    public ResponseEntity<Void> contactOwner(@PathVariable("id") Long id, @RequestBody ContactRequest request) {
        Property property = service.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (property.getUser() == null) {
            throw new RuntimeException("Property owner not found");
        }

        // phrase3
        try {
            brevoEmailService.sendContactEmail(
                    new BrevoEmailService.ContactFormData(
                            request.getSubject(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPhone(),
                            request.getMessage()),
                    property,
                    property.getUser());
        } catch (Exception e) {
            log.error("Failed to send contact email for property {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/score")
    public ResponseEntity<Property> updateScore(@PathVariable("id") Long id, @RequestBody PropertyScoreDTO scoreDTO) {
        return ResponseEntity.ok(service.updateScore(id, scoreDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Property>> getUserProperties(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(service.getUserProperties(userId, pageable));
    }

    @GetMapping("/user/{userId}/validate-limits")
    public ResponseEntity<Map<String, Object>> validatePlanLimits(
            @PathVariable("userId") Long userId,
            @RequestParam("listingType") Property.ListingType listingType,
            @RequestParam(value = "imageCount", defaultValue = "0") int imageCount) {
        return ResponseEntity.ok(service.validatePlanLimits(userId, listingType, imageCount));
    }
}
