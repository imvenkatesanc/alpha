package com.alpha.service.impl;

import com.alpha.dao.UserDao;
import com.alpha.model.*;
import com.alpha.model.Property.Attachment;
import com.alpha.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.el.PropertyNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PropertyService {

    private final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    private final PropertyRepository propertyRepository;
    private final UserDao userDao;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ShareRepository shareRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserDao userDao,
                           LikeRepository likeRepository, CommentRepository commentRepository,
                           ShareRepository shareRepository, BookmarkRepository bookmarkRepository) {
        this.propertyRepository = propertyRepository;
        this.userDao = userDao;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.shareRepository = shareRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    // Create Property
    public Property createProperty(Property property, MultipartFile file) throws Exception {
        Attachment attachment = saveAttachment(file);
        property.setAttachment(attachment);
        return propertyRepository.save(property);
    }

    // Update Property
    public Property updateProperty(Long id, Property property, MultipartFile file) throws Exception {
        Property existingProperty = getPropertyById(id);

        existingProperty.setName(property.getName());
        existingProperty.setDescription(property.getDescription());
        existingProperty.setPrice(property.getPrice());
        existingProperty.setType(property.getType());
        existingProperty.setAddress(property.getAddress());
        existingProperty.setAvailable(property.isAvailable());

        if (file != null && !file.isEmpty()) {
            Attachment newAttachment = saveAttachment(file);
            existingProperty.setAttachment(newAttachment);
        }

        return propertyRepository.save(existingProperty);
    }

    // Delete Property
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new IllegalArgumentException("Property not found with ID: " + id);
        }
        propertyRepository.deleteById(id);
    }

    // Get All Available Properties
    public List<Property> getAllAvailableProperties() {
        return propertyRepository.findByAvailable(true);
    }

    // Get Properties By User ID
    public List<Property> getPropertiesByUserId(Long userId) {
        List<Property> properties = propertyRepository.findByUserId(userId);
        logger.info("Found {} properties for user ID: {}", properties.size(), userId);
        return properties;
    }

    // Save Attachment
    private Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (fileName.contains("..")) {
            throw new Exception("Filename contains invalid path sequence: " + fileName);
        }

        String fileType = file.getContentType();
        if (!isValidFileType(fileType)) {
            throw new Exception("Invalid file type: " + fileType);
        }

        try {
            if (file.isEmpty()) {
                throw new Exception("Cannot save empty file: " + fileName);
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new Exception("File is too large: " + fileName);
            }

            return new Attachment(fileName, fileType, file.getBytes());
        } catch (Exception e) {
            throw new Exception("Could not save file: " + fileName, e);
        }
    }

    // Validate File Type
    private boolean isValidFileType(String fileType) {
        return fileType != null && (
                fileType.equals("application/pdf") ||
                        fileType.equals("image/jpeg") ||
                        fileType.equals("image/png")
        );
    }

    // Get Property By ID
    public Property getPropertyById(Long id) throws Exception {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new Exception("Property not found with ID: " + id));
    }

    // Check If User Is Property Owner
    public boolean isUserPropertyOwner(Long propertyId, Long userId) throws Exception {
        Property property = getPropertyById(propertyId);
        return Objects.equals(property.getUser().getId(), userId);
    }

    // Property Exists
    public boolean propertyExists(Long id) {
        return propertyRepository.existsById(id);
    }

    // Like service
    public void likeProperty(Long propertyId, Long userId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        Optional<User> userOpt = userDao.findById(userId);

        if (propertyOpt.isPresent() && userOpt.isPresent()) {
            // Check if the like already exists
            if (!likeRepository.existsByPropertyIdAndUserId(propertyId, userId)) {
                Like like = new Like();
                like.setProperty(propertyOpt.get());
                like.setUser(userOpt.get());
                likeRepository.save(like);
            } else {
                throw new RuntimeException("User already liked this property");
            }
        } else {
            throw new RuntimeException("Property or User not found");
        }
    }

    public void unlikeProperty(Long propertyId, Long userId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        Optional<User> userOpt = userDao.findById(userId);

        if (propertyOpt.isPresent() && userOpt.isPresent()) {
            Like like = likeRepository.findByPropertyIdAndUserId(propertyId, userId);
            if (like != null) {
                likeRepository.delete(like);
            } else {
                throw new RuntimeException("Like not found");
            }
        } else {
            throw new RuntimeException("Property or User not found");
        }
    }


    // Comments service

    public Comment addComment(Long propertyId, CommentRequestDto commentRequest) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setProperty(property);
        comment.setUser(userDao.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPropertyId(Long propertyId, int page, int size) {
        Page<Comment> commentPage = commentRepository.findByPropertyId(propertyId, PageRequest.of(page, size));
        return commentPage.getContent();
    }

    public void deleteComment(Long propertyId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getProperty().getId().equals(propertyId)) {
            throw new RuntimeException("Comment does not belong to this property");
        }

        commentRepository.delete(comment);
    }

    // Share Property
    public void shareProperty(Long propertyId, Long userId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        Optional<User> userOpt = userDao.findById(userId);

        if (propertyOpt.isPresent() && userOpt.isPresent()) {
            Share share = new Share();
            share.setProperty(propertyOpt.get());
            share.setUser(userOpt.get());
            shareRepository.save(share);

            // Increment the share count in the Property entity
            Property property = propertyOpt.get();
            property.setShareCount(property.getShareCount() + 1);
            propertyRepository.save(property);

            System.out.println("Property " + propertyId + " shared by user " + userId + ".");
        } else {
            throw new RuntimeException("Property or User not found");
        }
    }

    // Bookmark Property
    public void bookmarkProperty(Long propertyId, Long userId) {
        logger.info("Trying to bookmark propertyId: {} for userId: {}", propertyId, userId);
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        Optional<User> userOpt = userDao.findById(userId);

        if (propertyOpt.isPresent() && userOpt.isPresent()) {
            // Save bookmark
            Bookmark bookmark = new Bookmark();
            bookmark.setProperty(propertyOpt.get());
            bookmark.setUser(userOpt.get());
            bookmarkRepository.save(bookmark);
        } else {
            throw new RuntimeException("Property or User not found");
        }
    }
    public void deleteBookmark(Long propertyId, Long userId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        Optional<User> userOpt = userDao.findById(userId);

        if (propertyOpt.isPresent() && userOpt.isPresent()) {
            Bookmark bookmark = bookmarkRepository.findByPropertyIdAndUserId(propertyId, userId);
            if (bookmark != null) {
                bookmarkRepository.delete(bookmark);
            } else {
                throw new RuntimeException("Bookmark not found");
            }
        } else {
            throw new RuntimeException("Property or User not found");
        }
    }

    // Get Property Details
    public PropertyResponseData getPropertyDetails(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));

        int likesCount = property.getLikes() != null ? property.getLikes().size() : 0;
        int commentsCount = property.getComments() != null ? property.getComments().size() : 0;
        int sharesCount = property.getShares() != null ? property.getShares().size() : 0;

        return new PropertyResponseData(
                property.getId(),
                property.getName(),
                property.getDescription(),
                property.getPrice(),
                property.getType(),
                property.getAddress(),
                property.isAvailable(),
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/properties/files/")
                        .path(String.valueOf(property.getId()))
                        .toUriString(),
                likesCount,
                commentsCount,
                sharesCount,
                property.getUser() // Handle null case if needed
        );
    }
}