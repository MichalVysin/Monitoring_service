package com.example.monitoring_service.service;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.controller.ApplicationUserRequest;
import com.example.monitoring_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private ApplicationUser user1;
    private ApplicationUser user2;
    private List<ApplicationUser> users;

    @BeforeEach
    void setup() {

        userService = new UserServiceImpl(userRepository, new BCryptPasswordEncoder());

        user1 = new ApplicationUser("TestUsername", "test.username@example.com", "Password123");
        user2 = new ApplicationUser("TestUsername2", "test.username2@example.com", "Password123");

        users = new ArrayList<>();

    }

    @Test
    void whenCreatedUser_thanReturnUser() {

        ApplicationUserRequest requestedUser = new ApplicationUserRequest("TestUsername", "test.username@example.com", "Password123");

        userService.create(requestedUser);

        ArgumentCaptor<ApplicationUser> userArgumentCaptor = ArgumentCaptor.forClass(ApplicationUser.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertEquals(requestedUser.getUsername(), userArgumentCaptor.getValue().getUsername());
        assertEquals(requestedUser.getEmail(), userArgumentCaptor.getValue().getEmail());
        assertEquals(36, userArgumentCaptor.getValue().getAccessToken().length());
    }

    @Test
    void whenFindsAllUsers_thanReturnListOfUsers() {

        users.add(user1);
        users.add(user2);

        given(userRepository.findAll()).willReturn(users);

        List<ApplicationUser> allFoundedUsers = userService.findAll();

        verify(userRepository).findAll();

        assertEquals(users, allFoundedUsers);
        assertEquals(2, allFoundedUsers.size());
    }

    @Test
    void whenFindsUserById_thanReturnUser() {

        given(userRepository.getById(user1.getId())).willReturn(user1);

        ApplicationUser foundedUser = userService.findById(user1.getId());

        verify(userRepository).getById(user1.getId());

        assertEquals(user1, foundedUser);

    }

}