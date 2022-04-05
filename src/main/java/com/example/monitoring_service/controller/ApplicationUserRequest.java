package com.example.monitoring_service.controller;

import com.example.monitoring_service.security.Password;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public @Data class ApplicationUserRequest {

    @Size(min = 6, message = "Minimum length is 6 characters.")
    @Size(max = 255, message = "Maximum length is 255 characters.")
    private String username;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email is not valid.")
    @Size(max = 255, message = "Maximum length is 255 characters.")
    private String email;

    @Password
    private String password;

    public ApplicationUserRequest() {
    }

    public ApplicationUserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
