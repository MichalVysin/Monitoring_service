package com.example.monitoring_service.handling_errors;

import lombok.Data;

public @Data class ErrorField {

    private String fieldName;
    private String message;

    public ErrorField(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }
}
