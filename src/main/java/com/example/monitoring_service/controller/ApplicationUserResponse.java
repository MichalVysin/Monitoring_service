package com.example.monitoring_service.controller;

import lombok.Data;

public @Data class ApplicationUserResponse {

    private String username;
    private String email;
    private String accessToken;

    public ApplicationUserResponse() {
    }

    public ApplicationUserResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
