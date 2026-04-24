package com.maqc.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "properties")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String address;

    private String area;

    private Double latitude;
    private Double longitude;

    private Integer rooms; // Total number of rooms
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer yearBuilt;
    private Double squareFootage;

    private Boolean hasTerrace;
    private Boolean hasPool;
    private Boolean hasYard;
    private Integer indoorParking;
    private Integer outdoorParking;
    private Boolean hasStove;
    private Double annualRevenue; // Annual revenue income
    private Double annualExpenses; // Annual expenses

    private String contactName;
    private String contactPhone;
    private Boolean showContactInfo = true;

    private Double yield; // Net yield percentage for Plex properties

    private Double capRate; // Cap rate = (annualRevenue - annualExpenses) / price * 100

    @Enumerated(EnumType.STRING)
    private BusinessType businessType; // For commercial properties: RESTAURANT, STORE, OFFICE, etc.

    private Integer favoriteCount = 0;
    @Transient
    private Boolean isFavorite = false;

    private Integer viewCount = 0;

    private LocalDate moveInDate;

    private LocalDateTime publishDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    private PropertyStatus status = PropertyStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PropertyType type; // HOUSE, CONDO, PLEX, COMMERCIAL

    @Enumerated(EnumType.STRING)
    private ListingType listingType; // FOR_SALE, FOR_RENT

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    private List<String> imageUrls;

    @JsonProperty("email")
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Recommendation Score fields
    private Double recommendationScore; // Overall score out of 10
    private Double priceReasonablenessScore; // 0-3 points
    private Double rentalPerformanceScore; // 0-2 points
    private Double sellerMotivationScore; // 0-2 points
    private Double propertyConditionScore; // 0-1.5 points
    private Double transactionComplexityScore; // 0-1.5 points

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public Double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(Double squareFootage) {
        this.squareFootage = squareFootage;
    }

    public Boolean getHasTerrace() {
        return hasTerrace;
    }

    public void setHasTerrace(Boolean hasTerrace) {
        this.hasTerrace = hasTerrace;
    }

    public Boolean getHasPool() {
        return hasPool;
    }

    public void setHasPool(Boolean hasPool) {
        this.hasPool = hasPool;
    }

    public Boolean getHasYard() {
        return hasYard;
    }

    public void setHasYard(Boolean hasYard) {
        this.hasYard = hasYard;
    }

    public Integer getIndoorParking() {
        return indoorParking;
    }

    public void setIndoorParking(Integer indoorParking) {
        this.indoorParking = indoorParking;
    }

    public Integer getOutdoorParking() {
        return outdoorParking;
    }

    public void setOutdoorParking(Integer outdoorParking) {
        this.outdoorParking = outdoorParking;
    }

    public Boolean getHasStove() {
        return hasStove;
    }

    public void setHasStove(Boolean hasStove) {
        this.hasStove = hasStove;
    }

    public Double getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(Double annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public Double getAnnualExpenses() {
        return annualExpenses;
    }

    public void setAnnualExpenses(Double annualExpenses) {
        this.annualExpenses = annualExpenses;
    }

    public Double getCapRate() {
        return capRate;
    }

    public void setCapRate(Double capRate) {
        this.capRate = capRate;
    }

    private void calculateCapRate() {
        if (annualRevenue != null && annualExpenses != null && price != null && price.doubleValue() > 0) {
            double netIncome = annualRevenue - annualExpenses;
            double rawCapRate = (netIncome / price.doubleValue()) * 100;
            // Round to 2 decimal places to ensure consistent sorting
            this.capRate = Math.round(rawCapRate * 100.0) / 100.0;
        } else {
            this.capRate = null;
        }
    }

    public Double getYield() {
        return yield;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Boolean getShowContactInfo() {
        return showContactInfo;
    }

    public void setShowContactInfo(Boolean showContactInfo) {
        this.showContactInfo = showContactInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Recommendation Score Getters and Setters
    public Double getRecommendationScore() {
        return recommendationScore;
    }

    public void setRecommendationScore(Double recommendationScore) {
        this.recommendationScore = recommendationScore;
    }

    public Double getPriceReasonablenessScore() {
        return priceReasonablenessScore;
    }

    public void setPriceReasonablenessScore(Double priceReasonablenessScore) {
        this.priceReasonablenessScore = priceReasonablenessScore;
    }

    public Double getRentalPerformanceScore() {
        return rentalPerformanceScore;
    }

    public void setRentalPerformanceScore(Double rentalPerformanceScore) {
        this.rentalPerformanceScore = rentalPerformanceScore;
    }

    public Double getSellerMotivationScore() {
        return sellerMotivationScore;
    }

    public void setSellerMotivationScore(Double sellerMotivationScore) {
        this.sellerMotivationScore = sellerMotivationScore;
    }

    public Double getPropertyConditionScore() {
        return propertyConditionScore;
    }

    public void setPropertyConditionScore(Double propertyConditionScore) {
        this.propertyConditionScore = propertyConditionScore;
    }

    public Double getTransactionComplexityScore() {
        return transactionComplexityScore;
    }

    public void setTransactionComplexityScore(Double transactionComplexityScore) {
        this.transactionComplexityScore = transactionComplexityScore;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateCapRate();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateCapRate();
    }

    public enum PropertyType {
        HOUSE, CONDO, PLEX, COMMERCIAL
    }

    public enum BusinessType {
        RESTAURANT, STORE, OFFICE, RETAIL, INDUSTRIAL, MEDICAL, OTHER
    }

    public enum ListingType {
        FOR_SALE, FOR_RENT
    }

    public enum PropertyStatus {
        PENDING, APPROVED, REFUSED, EXPIRED
    }

    // Builder Pattern Implementation
    public static PropertyBuilder builder() {
        return new PropertyBuilder();
    }

    public static class PropertyBuilder {
        private String title;
        private BigDecimal price;
        private String address;
        private PropertyType type;
        private ListingType listingType;

        public PropertyBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PropertyBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public PropertyBuilder address(String address) {
            this.address = address;
            return this;
        }

        public PropertyBuilder type(PropertyType type) {
            this.type = type;
            return this;
        }

        public PropertyBuilder listingType(ListingType listingType) {
            this.listingType = listingType;
            return this;
        }

        public Property build() {
            Property p = new Property();
            p.setTitle(title);
            p.setPrice(price);
            p.setAddress(address);
            p.setType(type);
            p.setListingType(listingType);
            return p;
        }
    }
}
