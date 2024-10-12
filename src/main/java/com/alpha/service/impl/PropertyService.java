package com.alpha.service.impl;

import com.alpha.model.Property;
import com.alpha.model.Property.Attachment; // Assuming Attachment is an inner class in Property
import com.alpha.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Method to create a property with an attachment
    public Property createProperty(Property property, MultipartFile file) throws Exception {
        Attachment attachment = saveAttachment(file);
        property.setAttachment(attachment); // Set the attachment in the property
        return propertyRepository.save(property); // Save the property with the attachment
    }

    // Method to handle the file upload
    private Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Check for invalid path sequence
        if (fileName.contains("..")) {
            throw new Exception("Filename contains invalid path sequence: " + fileName);
        }

        // Validate file type (optional, add your desired file types)
        String fileType = file.getContentType();
        if (!isValidFileType(fileType)) {
            throw new Exception("Invalid file type: " + fileType);
        }

        try {
            // Check if the file is empty
            if (file.isEmpty()) {
                throw new Exception("Cannot save empty file: " + fileName);
            }
            // Check the size limit (e.g., 10 MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new Exception("File is too large: " + fileName);
            }

            // Create the attachment object
            return new Attachment(fileName, fileType, file.getBytes());
        } catch (Exception e) {
            throw new Exception("Could not save file: " + fileName, e);
        }
    }

    // Helper method to validate file types
    private boolean isValidFileType(String fileType) {
        return fileType != null && (
                fileType.equals("application/pdf") ||
                        fileType.equals("image/jpeg") ||
                        fileType.equals("image/png")
        );
    }

    public Property getPropertyById(Long id) throws Exception {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new Exception("Property not found with ID: " + id));
    }
}
