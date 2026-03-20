package com.maqc.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    private String province;
    private String postalCode;

    private Double latitude;
    private Double longitude;

    private Integer bedrooms;
    private Integer bathrooms;
    private Double squareFootage;

    @Enumerated(EnumType.STRING)
    private PropertyType type; // HOUSE, CONDO, PLEX, COMMERCIAL

    @Enumerated(EnumType.STRING)
    private ListingType listingType; // FOR_SALE, FOR_RENT

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    private List<String> imageUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public Double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(Double squareFootage) {
        this.squareFootage = squareFootage;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
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

    public void setAgent(User user) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PropertyType {
        HOUSE, CONDO, PLEX, COMMERCIAL
    }

    public enum ListingType {
        FOR_SALE, FOR_RENT
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
