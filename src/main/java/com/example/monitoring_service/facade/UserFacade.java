package com.example.monitoring_service.facade;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.controller.ApplicationUserRequest;

import javax.validation.Valid;
import java.util.List;

public interface UserFacade {

    List<ApplicationUser> getUsers();

    ApplicationUser getUser(Long id);

    ApplicationUser createUser(@Valid ApplicationUserRequest applicationUserRequest);
}
