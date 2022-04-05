package com.example.monitoring_service.controller;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.repository.UserRepository;
import com.example.monitoring_service.security.UserRole;
import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("NewClassNamingConvention")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ApplicationUser user;

    @BeforeEach
    void setup(){

        user = new ApplicationUser(
                "testUser",
                "test.user@example.com",
                passwordEncoder.encode("Password123"),
                UUID.randomUUID().toString(),
                UserRole.USER);

        userRepository.save(user);

    }

    @AfterEach
    void clean(){
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void whenTriesLoginWithNonExistingUsername_thanThrowUserNotFoundException_andReturnMessage() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                        "{" +
                                        "\"username\":\"testWrongUser\"," +
                                        "\"password\":\"Password123\"" +
                                        "}"
                        ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed."));
    }

    @Test
    @Transactional
    void whenLogsExistingUser_thanOk() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                        "{" +
                                        "\"username\":\"testUser\"," +
                                        "\"password\":\"Password123\"" +
                                        "}"
                        ))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION));

    }

}
