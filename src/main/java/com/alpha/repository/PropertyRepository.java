package com.alpha.repository;

import com.alpha.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Spring Data JPA will automatically implement this method based on the name
    List<Property> findByUserId(Long userId);
    List<Property> findByAvailable(boolean available);
    Optional<Property> findById(Long id);
}
