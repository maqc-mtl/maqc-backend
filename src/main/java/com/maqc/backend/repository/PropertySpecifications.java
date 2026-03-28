package com.maqc.backend.repository;

import com.maqc.backend.model.Property;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PropertySpecifications {

    public static Specification<Property> hasArea(String area) {
        return (root, query, cb) -> {
            if (area == null || area.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("area")), "%" + area.toLowerCase() + "%");
        };
    }

    public static Specification<Property> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("address")), pattern),
                    cb.like(cb.lower(root.get("area")), pattern));
        };
    }

    public static Specification<Property> hasType(Property.PropertyType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Property> hasListingType(Property.ListingType listingType) {
        return (root, query, cb) -> listingType == null ? cb.conjunction()
                : cb.equal(root.get("listingType"), listingType);
    }

    public static Specification<Property> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Property> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Property> hasBedroomsGreaterThanOrEqual(Integer minBedrooms) {
        return (root, query, cb) -> minBedrooms == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms);
    }

    public static Specification<Property> hasBathroomsGreaterThanOrEqual(Integer minBathrooms) {
        return (root, query, cb) -> minBathrooms == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("bathrooms"), minBathrooms);
    }

    public static Specification<Property> hasStatus(Property.PropertyStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Property> hasBusinessType(Property.BusinessType businessType) {
        return (root, query, cb) -> businessType == null ? cb.conjunction() : cb.equal(root.get("businessType"), businessType);
    }
}
