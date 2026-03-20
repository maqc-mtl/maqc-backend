package com.maqc.backend.controller;

import com.maqc.backend.dto.ContactRequest;
import com.maqc.backend.model.Property;
import com.maqc.backend.service.EmailService;
import com.maqc.backend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService service;
    private final EmailService emailService;

    @GetMapping("/public/search")
    public ResponseEntity<Page<Property>> searchProperties(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "type", required = false) Property.PropertyType type,
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
                listingType,
                minPrice,
                maxPrice,
                minBedrooms,
                minBathrooms,
                pageable));
    }

    @GetMapping("/public")
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(service.getAllProperties());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable("id") Long id) {
        return service.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        return ResponseEntity.ok(service.createProperty(property));
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

    @PostMapping("/{id}/contact")
    public ResponseEntity<Void> contactOwner(@PathVariable("id") Long id, @RequestBody ContactRequest request) {
        Property property = service.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (property.getUser() == null) {
            throw new RuntimeException("Property owner not found");
        }

        try {
            emailService.sendContactEmail(
                    new EmailService.ContactFormData(
                            request.getSubject(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPhone(),
                            request.getMessage()),
                    property,
                    property.getUser());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}
