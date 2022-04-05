package com.example.monitoring_service.service;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.controller.ApplicationUserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    ApplicationUser create(ApplicationUserRequest applicationUserRequest);

    List<ApplicationUser> findAll();

    ApplicationUser findById(Long id);

}
