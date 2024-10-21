package com.alpha.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Property Response.
 */
@Data
@NoArgsConstructor
public class PropertyResponseData {

    private Long id; // ID for identification
    private String name; // Changed to be consistent with property model
    private String description;
    private BigDecimal price;
    private String type;
    private String address;
    private boolean available;
    private String downloadURL; // URL for downloading files
    private int likesCount;     // Number of likes
    private int commentsCount;  // Number of comments
    private int sharesCount;    // Number of shares
    private User propertyOwner; // Owner of the property

    // Constructor for creating from property details
    public PropertyResponseData(Long id, String name, String description, BigDecimal price, String type,
                                String address, boolean available, String downloadURL,
                                int likesCount, int commentsCount, int sharesCount, User propertyOwner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.address = address;
        this.available = available;
        this.downloadURL = downloadURL;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.sharesCount = sharesCount;
        this.propertyOwner = propertyOwner;
    }

    // Optionally, add another constructor without propertyOwner
    public PropertyResponseData(Long id, String name, String description, BigDecimal price, String type,
                                String address, boolean available, String downloadURL,
                                int likesCount, int commentsCount, int sharesCount) {
        this(id, name, description, price, type, address, available, downloadURL,
                likesCount, commentsCount, sharesCount, null); // propertyOwner defaults to null
    }
}
