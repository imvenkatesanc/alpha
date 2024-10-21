package com.alpha.repository;


import com.alpha.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Bookmark findByPropertyIdAndUserId(Long propertyId, Long userId);
}
