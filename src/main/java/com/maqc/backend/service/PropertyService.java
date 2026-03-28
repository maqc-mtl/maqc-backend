package com.maqc.backend.service;

import com.maqc.backend.model.Property;
import com.maqc.backend.repository.PropertyRepository;
import com.maqc.backend.repository.PropertySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository repository;

    public Page<Property> searchProperties(
            String keyword,
            String area,
            Property.PropertyType type,
            Property.BusinessType businessType,
            Property.ListingType listingType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minBedrooms,
            Integer minBathrooms,
            Property.PropertyStatus status,
            Pageable pageable) {
        Specification<Property> spec = Specification.where(PropertySpecifications.hasKeyword(keyword))

                .and(PropertySpecifications.hasArea(area))
                .and(PropertySpecifications.hasType(type))
                .and(PropertySpecifications.hasBusinessType(businessType))
                .and(PropertySpecifications.hasListingType(listingType))
                .and(PropertySpecifications.priceGreaterThanOrEqual(minPrice))
                .and(PropertySpecifications.priceLessThanOrEqual(maxPrice))
                .and(PropertySpecifications.hasBedroomsGreaterThanOrEqual(minBedrooms))
                .and(PropertySpecifications.hasBathroomsGreaterThanOrEqual(minBathrooms))
                .and(PropertySpecifications.hasStatus(status));

        return repository.findAll(spec, pageable);
    }

    public List<Property> getAllProperties() {
        return repository.findAll();
    }

    public Optional<Property> getPropertyById(Long id) {
        return repository.findById(id);
    }

    public List<Property> getPropertiesByListingType(Property.ListingType listingType) {
        return repository.findByListingType(listingType);
    }

    public Property createProperty(Property property) {
        return repository.save(property);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setTitle(propertyDetails.getTitle());
        property.setDescription(propertyDetails.getDescription());
        property.setPrice(propertyDetails.getPrice());
        property.setAddress(propertyDetails.getAddress());
        property.setArea(propertyDetails.getArea());
        property.setPostalCode(propertyDetails.getPostalCode());
        property.setRooms(propertyDetails.getRooms());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setYearBuilt(propertyDetails.getYearBuilt());
        property.setSquareFootage(propertyDetails.getSquareFootage());
        property.setHasTerrace(propertyDetails.getHasTerrace());
        property.setHasPool(propertyDetails.getHasPool());
        property.setHasYard(propertyDetails.getHasYard());
        property.setAnnualRent(propertyDetails.getAnnualRent());
        property.setAnnualExpenses(propertyDetails.getAnnualExpenses());
        property.setType(propertyDetails.getType());
        property.setBusinessType(propertyDetails.getBusinessType());
        property.setListingType(propertyDetails.getListingType());
        property.setImageUrls(propertyDetails.getImageUrls());
        property.setMoveInDate(propertyDetails.getMoveInDate());

        return repository.save(property);
    }

    public void deleteProperty(Long id) {
        repository.deleteById(id);
    }

    public Property toggleFavorite(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Toggle the favorite status
        property.setIsFavorite(!property.getIsFavorite());
        // Update favorite count accordingly
        property.setFavoriteCount(property.getIsFavorite() ? property.getFavoriteCount() + 1
                : Math.max(0, property.getFavoriteCount() - 1));

        return repository.save(property);
    }

    public Property incrementViewCount(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Increment view count
        property.setViewCount(property.getViewCount() + 1);

        return repository.save(property);
    }

    public Page<Property> getFavoriteProperties(Pageable pageable) {
        return repository.findByIsFavoriteTrue(pageable);
    }

    public Property approveProperty(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(Property.PropertyStatus.APPROVED);
        property.setPublishDate(LocalDateTime.now());
        return repository.save(property);
    }

    public Property refuseProperty(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(Property.PropertyStatus.REFUSED);
        return repository.save(property);
    }
}
