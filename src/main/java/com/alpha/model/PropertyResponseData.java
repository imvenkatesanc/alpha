package com.alpha.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseData {

    private String fileName;
    private String downloadURL;
    private String fileType;
    private long fileSize;

    // Property-specific fields
    private String propertyName;
    private String description;
    private BigDecimal price;
}
