package com.example.monitoring_service.controller;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public @Data class MonitoredEndpointRequest {

    @NotBlank(message = "Name cannot be empty.")
    @Size(max = 255, message = "Maximum length is 255 characters.")
    private String name;

    @NotBlank(message = "Url cannot be empty.")
    @URL(message = "Url is not valid.")
    @Size(max = 255, message = "Maximum length is 255 characters.")
    private String url;

    public MonitoredEndpointRequest() {
    }

    public MonitoredEndpointRequest(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
