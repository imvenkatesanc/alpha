package com.alpha.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserPropertiesResponse {
    private Long userId;  // ID of the user
    private List<PropertyResponseData> properties;  // List of properties associated with the user

    public UserPropertiesResponse(Long userId, List<PropertyResponseData> properties) {
        this.userId = userId;
        this.properties = properties;
    }
}
