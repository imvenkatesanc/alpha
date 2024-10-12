package com.alpha.controller;
import com.alpha.model.Property;
import com.alpha.model.Property.Attachment; // Import the inner Attachment class
import com.alpha.model.PropertyResponseData;
import com.alpha.service.impl.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // Only Admin can create a property
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(@RequestParam("property") String propertyJson,
                                                   @RequestParam("file") MultipartFile file) throws Exception {
        // Parse the JSON string into a Property object
        ObjectMapper objectMapper = new ObjectMapper();
        Property property = objectMapper.readValue(propertyJson, Property.class);

        // Save the attachment
        Attachment attachment = new Attachment(file.getOriginalFilename(), file.getContentType(), file.getBytes());

        // Set the attachment to the property
        property.setAttachment(attachment);

        // Save the property
        Property createdProperty = propertyService.createProperty(property, file);
        return ResponseEntity.ok(createdProperty);
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<PropertyResponseData> downloadFile(@PathVariable Long id) throws Exception {
        // Fetch the property by ID
        Property property = propertyService.getPropertyById(id);
        Attachment attachment = property.getAttachment();

        // Generate the download URL
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/properties/files/")
                .path(String.valueOf(id))
                .toUriString();

        // Create the response object with file and property details
        PropertyResponseData responseData = new PropertyResponseData(
                attachment.getFileName(),
                downloadURL,
                attachment.getFileType(),
                attachment.getData().length,
                property.getName(),  // Property name
                property.getDescription(),  // Property description
                property.getPrice()  // Property price
        );

        return ResponseEntity.ok(responseData);
    }

    // This will handle the actual file download
    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> downloadActualFile(@PathVariable Long id) throws Exception {
        Property property = propertyService.getPropertyById(id);
        Attachment attachment = property.getAttachment();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}

