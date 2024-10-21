package com.alpha.model;

import java.util.List;

public class AvailablePropertiesResponse {
    private List<PropertyResponseData> properties;

    public AvailablePropertiesResponse(List<PropertyResponseData> properties) {
        this.properties = properties;
    }

    public List<PropertyResponseData> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyResponseData> properties) {
        this.properties = properties;
    }
}
