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
            Property.ListingType listingType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minBedrooms,
            Integer minBathrooms,
            Pageable pageable) {
        Specification<Property> spec = Specification.where(PropertySpecifications.hasKeyword(keyword))
                .and(PropertySpecifications.hasArea(area))
                .and(PropertySpecifications.hasType(type))
                .and(PropertySpecifications.hasListingType(listingType))
                .and(PropertySpecifications.priceGreaterThanOrEqual(minPrice))
                .and(PropertySpecifications.priceLessThanOrEqual(maxPrice))
                .and(PropertySpecifications.hasBedroomsGreaterThanOrEqual(minBedrooms))
                .and(PropertySpecifications.hasBathroomsGreaterThanOrEqual(minBathrooms));

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
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setSquareFootage(propertyDetails.getSquareFootage());
        property.setType(propertyDetails.getType());
        property.setListingType(propertyDetails.getListingType());
        property.setImageUrls(propertyDetails.getImageUrls());

        return repository.save(property);
    }

    public void deleteProperty(Long id) {
        repository.deleteById(id);
    }
}
