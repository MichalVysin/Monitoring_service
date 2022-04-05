package com.example.monitoring_service.controller;

import com.example.monitoring_service.facade.UserFacade;
import com.example.monitoring_service.model.ApplicationUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class UserController {

    private final UserFacade userFacade;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserFacade userFacade, ModelMapper modelMapper) {
        this.userFacade = userFacade;
        this.modelMapper = modelMapper;
    }


    @GetMapping(value = "/users")
    @ResponseStatus(HttpStatus.OK)
    public List<ApplicationUserResponse> getUsers(){
        List<ApplicationUser> applicationUsers = userFacade.getUsers();
        return applicationUsers
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationUserResponse getUser(@PathVariable("userId") Long userId){
        return convertToResponse(userFacade.getUser(userId));
    }

    @PostMapping(value = "/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationUserResponse createUser(@RequestBody ApplicationUserRequest applicationUserRequest){
        return convertToResponse(userFacade.createUser(applicationUserRequest));
    }


    private ApplicationUserResponse convertToResponse(ApplicationUser applicationUser){
        return modelMapper.map(applicationUser, ApplicationUserResponse.class);
    }
}
