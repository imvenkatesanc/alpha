package com.alpha.repository;

import com.alpha.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPropertyIdAndUserId(Long propertyId, Long userId);

    Like findByPropertyIdAndUserId(Long propertyId, Long userId);
}
