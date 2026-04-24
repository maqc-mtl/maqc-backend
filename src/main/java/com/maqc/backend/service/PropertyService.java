package com.maqc.backend.service;

import com.maqc.backend.config.PlanLimits;
import com.maqc.backend.dto.PropertyScoreDTO;
import com.maqc.backend.exception.PlanLimitExceededException;
import com.maqc.backend.model.Property;
import com.maqc.backend.repository.PropertyRepository;
import com.maqc.backend.repository.PropertySpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.maqc.backend.repository.UserRepository;
import com.maqc.backend.repository.FavoriteRepository;
import com.maqc.backend.model.User;
import com.maqc.backend.model.Favorite;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyService {

    private final PropertyRepository repository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlanLimits planLimits;

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

        Page<Property> results = repository.findAll(spec, pageable);
        return results;
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

    public Property createProperty(Property property, List<MultipartFile> files) throws IOException {
        // Associate user if email is provided
        User user = null;
        if (property.getEmail() != null) {
            String email = property.getEmail().toLowerCase().trim();
            log.info("Attempting to associate property with user email: {}", email);
            user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                log.info("Found user: {} for email: {}", user.getId(), email);
                property.setUser(user);
            } else {
                log.warn("No user found for email: {}", email);
            }
        } else {
            log.warn("No email provided in property data for association");
        }

        // Enforce plan limits if user is identified
        if (user != null) {
            enforcePlanLimits(user, property.getListingType(), files != null ? files.size() : 0);
        }

        // Upload images
        if (files != null && !files.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = s3Service.uploadFile(file);
                imageUrls.add(url);
            }
            property.setImageUrls(imageUrls);
        }

        // // Set expiration date based on user's plan
        // if (user != null) {
        // property.setExpirationDate(planLimits.calculateExpirationDate(user.getPlanType()));
        // }

        return repository.save(property);
    }

    private void enforcePlanLimits(User user, Property.ListingType listingType, int imageCount) {
        PlanLimits.Limits limits = planLimits.getLimitsForPlan(user.getPlanType());

        // Check image limit
        if (imageCount > limits.maxImagesPerListing) {
            throw new PlanLimitExceededException(String.format(
                    "Image limit exceeded. Your plan (%s) allows up to %d images per listing. You attempted to upload %d images.",
                    user.getPlanType(), limits.maxImagesPerListing, imageCount));
        }

        // Count active properties (status = APPROVED or PENDING) for the specific
        // listing type
        List<Property.PropertyStatus> activeStatuses = List.of(Property.PropertyStatus.APPROVED,
                Property.PropertyStatus.PENDING);
        long currentCount = 0;
        if (listingType == Property.ListingType.FOR_RENT) {
            currentCount = repository.countByUserAndListingTypeAndStatusIn(user, Property.ListingType.FOR_RENT,
                    activeStatuses);
        } else if (listingType == Property.ListingType.FOR_SALE) {
            currentCount = repository.countByUserAndListingTypeAndStatusIn(user, Property.ListingType.FOR_SALE,
                    activeStatuses);
        }

        int maxAllowed = listingType == Property.ListingType.FOR_RENT ? limits.maxRentalListings
                : limits.maxSaleListings;

        if (currentCount >= maxAllowed) {
            String listingTypeStr = listingType == Property.ListingType.FOR_RENT ? "rental listings"
                    : "properties for sale";
            throw new RuntimeException(String.format(
                    "Listing limit exceeded. Your plan (%s) allows up to %d %s. You currently have %d active %s.",
                    user.getPlanType(), maxAllowed, listingTypeStr, currentCount, listingTypeStr));
        }
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setTitle(propertyDetails.getTitle());
        property.setDescription(propertyDetails.getDescription());
        property.setPrice(propertyDetails.getPrice());
        property.setAddress(propertyDetails.getAddress());
        property.setArea(propertyDetails.getArea());
        property.setRooms(propertyDetails.getRooms());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setYearBuilt(propertyDetails.getYearBuilt());
        property.setSquareFootage(propertyDetails.getSquareFootage());
        property.setHasTerrace(propertyDetails.getHasTerrace());
        property.setHasPool(propertyDetails.getHasPool());
        property.setHasYard(propertyDetails.getHasYard());
        property.setAnnualRevenue(propertyDetails.getAnnualRevenue());
        property.setAnnualExpenses(propertyDetails.getAnnualExpenses());
        property.setIndoorParking(propertyDetails.getIndoorParking());
        property.setOutdoorParking(propertyDetails.getOutdoorParking());
        property.setHasStove(propertyDetails.getHasStove());
        property.setType(propertyDetails.getType());
        property.setBusinessType(propertyDetails.getBusinessType());
        property.setListingType(propertyDetails.getListingType());
        property.setImageUrls(propertyDetails.getImageUrls());
        property.setMoveInDate(propertyDetails.getMoveInDate());
        property.setContactName(propertyDetails.getContactName());
        property.setContactPhone(propertyDetails.getContactPhone());
        property.setShowContactInfo(propertyDetails.getShowContactInfo());
        property.setEmail(propertyDetails.getEmail());

        return repository.save(property);
    }

    public void deleteProperty(Long id) {
        repository.deleteById(id);
    }

    public Property toggleFavorite(Long id, Long userId) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (userId == null) {
            throw new RuntimeException("User must be logged in to favorite properties");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Favorite> existing = favoriteRepository.findByUserAndProperty(user, property);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            property.setFavoriteCount(Math.max(0, property.getFavoriteCount() - 1));
            property.setIsFavorite(false);
        } else {
            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setProperty(property);
            favoriteRepository.save(fav);
            property.setFavoriteCount(property.getFavoriteCount() + 1);
            property.setIsFavorite(true);
        }

        return repository.save(property);
    }

    public Property incrementViewCount(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Increment view count
        property.setViewCount(property.getViewCount() + 1);

        return repository.save(property);
    }

    public Page<Property> getFavoriteProperties(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findByUser(user, pageable).map(fav -> {
            Property p = fav.getProperty();
            p.setIsFavorite(true);
            return p;
        });
    }

    public void checkFavoriteStatus(Property property, Long userId) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                boolean isFav = favoriteRepository.existsByUserAndProperty(user, property);
                property.setIsFavorite(isFav);
            });
        }
    }

    public Property approveProperty(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(Property.PropertyStatus.APPROVED);
        property.setPublishDate(LocalDateTime.now());

        // Set expiration date based on user's plan
        if (property.getUser() != null) {
            property.setExpirationDate(planLimits.calculateExpirationDate(property.getUser().getPlanType()));
        }

        return repository.save(property);
    }

    public Property refuseProperty(Long id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(Property.PropertyStatus.REFUSED);
        return repository.save(property);
    }

    public Property updatePropertyStatus(Long id, Property.PropertyStatus status) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(status);
        return repository.save(property);
    }

    public Property updateScore(Long id, PropertyScoreDTO scoreDTO) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setPriceReasonablenessScore(scoreDTO.getPriceReasonablenessScore());
        property.setRentalPerformanceScore(scoreDTO.getRentalPerformanceScore());
        property.setSellerMotivationScore(scoreDTO.getSellerMotivationScore());
        property.setPropertyConditionScore(scoreDTO.getPropertyConditionScore());
        property.setTransactionComplexityScore(scoreDTO.getTransactionComplexityScore());

        // Calculate overall score (sum of all criteria)
        double overallScore = 0.0;
        if (scoreDTO.getPriceReasonablenessScore() != null)
            overallScore += scoreDTO.getPriceReasonablenessScore();
        if (scoreDTO.getRentalPerformanceScore() != null)
            overallScore += scoreDTO.getRentalPerformanceScore();
        if (scoreDTO.getSellerMotivationScore() != null)
            overallScore += scoreDTO.getSellerMotivationScore();
        if (scoreDTO.getPropertyConditionScore() != null)
            overallScore += scoreDTO.getPropertyConditionScore();
        if (scoreDTO.getTransactionComplexityScore() != null)
            overallScore += scoreDTO.getTransactionComplexityScore();

        property.setRecommendationScore(Math.round(overallScore * 10.0) / 10.0); // Round to 1 decimal place

        return repository.save(property);
    }

    public Page<Property> getUserProperties(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repository.findByUser(user, pageable);
    }

    public Map<String, Object> validatePlanLimits(Long userId, Property.ListingType listingType, int imageCount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PlanLimits.Limits limits = planLimits.getLimitsForPlan(user.getPlanType());

        // Count active properties (PENDING or APPROVED)
        List<Property.PropertyStatus> activeStatuses = List.of(Property.PropertyStatus.PENDING,
                Property.PropertyStatus.APPROVED);
        long currentCount = repository.countByUserAndListingTypeAndStatusIn(user, listingType, activeStatuses);

        int maxAllowed = listingType == Property.ListingType.FOR_RENT ? limits.maxRentalListings
                : limits.maxSaleListings;
        boolean listingsOk = currentCount < maxAllowed;
        boolean imagesOk = imageCount <= limits.maxImagesPerListing;

        Map<String, Object> result = new HashMap<>();
        result.put("planType", user.getPlanType().toString());
        result.put("listingType", listingType.toString());
        result.put("currentListings", currentCount);
        result.put("maxListings", maxAllowed);
        result.put("listingsOk", listingsOk);
        result.put("imageCount", imageCount);
        result.put("maxImages", limits.maxImagesPerListing);
        result.put("imagesOk", imagesOk);
        result.put("overallOk", listingsOk && imagesOk);

        return result;
    }
}
