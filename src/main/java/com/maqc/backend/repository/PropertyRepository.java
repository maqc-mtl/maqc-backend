package com.maqc.backend.repository;

import com.maqc.backend.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.maqc.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findByArea(String area);

    List<Property> findByListingType(Property.ListingType listingType);

    Page<Property> findByUser(User user, Pageable pageable);

    Page<Property> findByIsFavoriteTrue(Pageable pageable);

    long countByUserAndListingTypeAndStatusIn(User user, Property.ListingType listingType,
            List<Property.PropertyStatus> statuses);

    long countByUserAndStatusIn(User user, List<Property.PropertyStatus> statuses);

    List<Property> findByStatusAndExpirationDateBefore(Property.PropertyStatus status, LocalDateTime expirationDate);
}
