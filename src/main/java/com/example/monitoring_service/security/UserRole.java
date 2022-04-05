package com.example.monitoring_service.security;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.monitoring_service.security.UserPermission.*;

public enum UserRole {
    USER(Sets.newHashSet(ENDPOINT_READ, ENDPOINT_WRITE, RESULT_READ)),
    ADMIN(Sets.newHashSet(USER_READ, ENDPOINT_READ, ENDPOINT_WRITE, RESULT_READ));

    @Getter private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());

        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return permissions;
    }
}
