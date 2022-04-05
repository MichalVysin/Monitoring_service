package com.example.monitoring_service.security.jwt;

import lombok.Data;

public @Data class UsernameAndPassAuthRequest {

    private String username;
    private String password;

}
