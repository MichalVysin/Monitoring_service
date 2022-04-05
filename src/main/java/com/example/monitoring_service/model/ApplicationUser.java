package com.example.monitoring_service.model;

import com.example.monitoring_service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public @Data class ApplicationUser extends _Base implements UserDetails{

    private String username;
    private String email;
    private String password;

    @Column(name = "is_account_non_expired")
    private boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked")
    private boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired")
    private boolean isCredentialsNonExpired;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "access_token")
    private String accessToken;

    @OneToMany(mappedBy = "user")
    private List<MonitoredEndpoint> monitoredEndpoints;

    public ApplicationUser() {
    }

    public ApplicationUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public ApplicationUser(String username, String email, String accessToken, String password) {
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
        this.password = password;
    }

    public ApplicationUser(long id, String username, String email, String password) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public ApplicationUser(String username, String email, String password , List<MonitoredEndpoint> monitoredEndpoints) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.monitoredEndpoints = monitoredEndpoints;
    }

    public ApplicationUser(String username, String email, String password, String accessToken, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.role = role;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getGrantedAuthorities();
    }
}
