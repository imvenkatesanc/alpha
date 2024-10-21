package com.alpha.controller;

import com.alpha.model.*;
import com.alpha.repository.CommentRepository;
import com.alpha.repository.PropertyRepository;
import com.alpha.service.impl.PropertyService;
import com.alpha.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private CommentRepository commentRepository;
    private final PropertyService propertyService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    public PropertyController(PropertyService propertyService, UserService userService, PropertyRepository propertyRepository) {
        this.propertyService = propertyService;
        this.userService = userService;
        this.propertyRepository = propertyRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(@RequestParam("property") String propertyJson,
                                                   @RequestParam("file") MultipartFile file) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Property property = objectMapper.readValue(propertyJson, Property.class);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        property.setUser(currentUser);
        Property.Attachment attachment = new Property.Attachment(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        property.setAttachment(attachment);

        Property createdProperty = propertyService.createProperty(property, file);
        return ResponseEntity.ok(createdProperty);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<AvailablePropertiesResponse> getAllAvailableProperties() {
        List<Property> properties = propertyService.getAllAvailableProperties();
        List<PropertyResponseData> responseDataList = properties.stream()
                .map(property -> {
                    String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/properties/files/")
                            .path(String.valueOf(property.getId()))
                            .toUriString();

                    int likesCount = getCount(property.getLikes());
                    int commentsCount = getCount(property.getComments());
                    int sharesCount = getCount(property.getShares());

                    return new PropertyResponseData(
                            property.getId(),
                            property.getName(),
                            property.getDescription(),
                            property.getPrice(),
                            property.getType(),
                            property.getAddress(),
                            property.isAvailable(),
                            downloadURL,
                            likesCount,
                            commentsCount,
                            sharesCount,
                            property.getUser()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AvailablePropertiesResponse(responseDataList));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserPropertiesResponse> getPropertiesByUserId(@PathVariable Long userId) {
        List<Property> properties = propertyService.getPropertiesByUserId(userId);
        List<PropertyResponseData> responseDataList = properties.stream()
                .map(property -> {
                    String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/properties/files/")
                            .path(String.valueOf(property.getId()))
                            .toUriString();

                    int likesCount = getCount(property.getLikes());
                    int commentsCount = getCount(property.getComments());
                    int sharesCount = getCount(property.getShares());

                    return new PropertyResponseData(
                            property.getId(),
                            property.getName(),
                            property.getDescription(),
                            property.getPrice(),
                            property.getType(),
                            property.getAddress(),
                            property.isAvailable(),
                            downloadURL,
                            likesCount,
                            commentsCount,
                            sharesCount,
                            property.getUser()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserPropertiesResponse(userId, responseDataList));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<PropertyResponseData> downloadFile(@PathVariable Long id) {
        try {
            Property property = propertyService.getPropertyById(id);

            if (property == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/properties/files/")
                    .path(String.valueOf(id))
                    .toUriString();

            int likesCount = getCount(property.getLikes());
            int commentsCount = getCount(property.getComments());
            int sharesCount = getCount(property.getShares());

            PropertyResponseData responseData = new PropertyResponseData(
                    property.getId(),
                    property.getName(),
                    property.getDescription(),
                    property.getPrice(),
                    property.getType(),
                    property.getAddress(),
                    property.isAvailable(),
                    downloadURL,
                    likesCount,
                    commentsCount,
                    sharesCount,
                    property.getUser()
            );

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            logger.error("Error downloading file for property ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> downloadActualFile(@PathVariable Long id) throws Exception {
        Property property = propertyService.getPropertyById(id);
        Property.Attachment attachment = property.getAttachment();

        if (attachment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Handle missing attachment
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

    @PreAuthorize("hasRole('ADMIN') or @propertyService.isUserPropertyOwner(#id, getCurrentUser().user.id)")
    @PutMapping("/update/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id,
                                                   @RequestParam("property") String propertyJson,
                                                   @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Property property = objectMapper.readValue(propertyJson, Property.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        User currentUser = (User) authentication.getPrincipal();
        property.setUser(currentUser);

        Property updatedProperty = propertyService.updateProperty(id, property, file);
        return ResponseEntity.ok(updatedProperty);
    }

    @PreAuthorize("hasRole('ADMIN') or @propertyService.isUserPropertyOwner(#id, getCurrentUser().user.id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    // Post Actions Like
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{propertyId}/like")
    public ResponseEntity<Void> likeProperty(@PathVariable Long propertyId, @RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId"); // Assuming the JSON has a userId field
        propertyService.likeProperty(propertyId, userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{propertyId}/like")
    public ResponseEntity<Void> unlikeProperty(@PathVariable Long propertyId, @RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId"); // Assuming the JSON has a userId field
        propertyService.unlikeProperty(propertyId, userId);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    // comment controller
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{propertyId}/comment")
    public ResponseEntity<Comment> postComment(@PathVariable Long propertyId,
                                               @RequestBody CommentRequestDto commentRequest) {
        Comment createdComment = propertyService.addComment(propertyId, commentRequest);
        return ResponseEntity.ok(createdComment);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{propertyId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long propertyId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        List<Comment> comments = propertyService.getCommentsByPropertyId(propertyId, page, size);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{propertyId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long propertyId,
                                              @PathVariable Long commentId) {
        propertyService.deleteComment(propertyId, commentId);
        return ResponseEntity.noContent().build();
    }

    // bookmark controller
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{propertyId}/bookmark")
    public ResponseEntity<Void> bookmarkProperty(@PathVariable Long propertyId, @RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId"); // Assuming the JSON has a userId field
        propertyService.bookmarkProperty(propertyId, userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{propertyId}/bookmark")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long propertyId, @RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId"); // Assuming the JSON has a userId field
        propertyService.deleteBookmark(propertyId, userId);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }
    // Share controller
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{propertyId}/share")
    public ResponseEntity<Void> shareProperty(@PathVariable Long propertyId, @RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId"); // Assuming the JSON has a userId field
        propertyService.shareProperty(propertyId, userId);
        return ResponseEntity.ok().build();
    }

    // get prop
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponseData> getPropertyDetails(@PathVariable Long propertyId) {
        PropertyResponseData propertyDto = propertyService.getPropertyDetails(propertyId);
        return ResponseEntity.ok(propertyDto);
    }

    private int getCount(List<?> list) {
        return list != null ? list.size() : 0;
    }
}
