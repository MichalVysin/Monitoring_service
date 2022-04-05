package com.example.monitoring_service.security;

import lombok.Getter;

public enum UserPermission {
    USER_READ("user:read"),
    ENDPOINT_READ("endpoint:read"),
    ENDPOINT_WRITE("endpoint:write"),
    RESULT_READ("result:read");

    @Getter private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }
}
