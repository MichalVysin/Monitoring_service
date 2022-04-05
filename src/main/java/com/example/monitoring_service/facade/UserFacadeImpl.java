package com.example.monitoring_service.facade;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.controller.ApplicationUserRequest;
import com.example.monitoring_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class UserFacadeImpl implements UserFacade{

    private final UserService userService;

    @Autowired
    public UserFacadeImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApplicationUser> getUsers() {
        return userService.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUser(authentication, #userId)")
    public ApplicationUser getUser(Long userId) {
        return userService.findById(userId);
    }

    @Override
    @PreAuthorize("permitAll()")
    public ApplicationUser createUser(@Valid ApplicationUserRequest applicationUserRequest) {
        return userService.create(applicationUserRequest);
    }
}
