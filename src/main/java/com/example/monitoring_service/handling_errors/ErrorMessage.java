package com.example.monitoring_service.handling_errors;

import lombok.Data;

public @Data class ErrorMessage {

    private String message;

    public ErrorMessage(String message) {
        this.message = message;
    }
}
