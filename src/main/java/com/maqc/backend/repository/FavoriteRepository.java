package com.maqc.backend.repository;

import com.maqc.backend.model.Favorite;
import com.maqc.backend.model.Property;
import com.maqc.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Optional<Favorite> findByUserAndProperty(User user, Property property);
    
    boolean existsByUserAndProperty(User user, Property property);
    
    Page<Favorite> findByUser(User user, Pageable pageable);
    
    List<Favorite> findByUser(User user);
}
