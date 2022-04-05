package com.example.monitoring_service.controller;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("NewClassNamingConvention")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ApplicationUser user;
    private final List<ApplicationUser> allUsers = new ArrayList<>();

    @BeforeEach
    void setup() {

        user = new ApplicationUser(
                        "TestUsername",
                        "test.username@exam.com",
                        passwordEncoder.encode("Password123"));

        allUsers.addAll(Arrays.asList(
                user,
                new ApplicationUser("TUsername2", "t.username2@example.com",
                        passwordEncoder.encode("Password123")),
                new ApplicationUser("TUsername3", "t.username3@example.com",
                        passwordEncoder.encode("Password123")),
                new ApplicationUser("TUsername4", "t.username4@example.com",
                        passwordEncoder.encode("Password123"))));

        userRepository.saveAllAndFlush(allUsers);

    }

    @AfterEach
    void clear() {

        userRepository.deleteAll();
        allUsers.clear();

    }


    @Test
    @Transactional
    @WithMockUser(username = "TestUsername")
    void whenGetsUserById_thanReturnResponseUser() throws Exception {

        mockMvc.perform(get("/user/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void whenGetsUsers_thanReturnAllResponseUsersList() throws Exception {

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].username").value(containsInAnyOrder(
                        "TestUsername",
                        "TUsername2",
                        "TUsername3",
                        "TUsername4"
                )))
                .andExpect(jsonPath("$[*].email").value(containsInAnyOrder(
                        "test.username@exam.com",
                        "t.username2@example.com",
                        "t.username3@example.com",
                        "t.username4@example.com"
                )));

    }


    @Test
    @Transactional
    void whenCreatesUser_thanReturnResponseUser() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"PostedUser\"," +
                                "\"email\":\"posted.user@example.com\"," +
                                "\"password\":\"Password123\"" +
                                "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("PostedUser"))
                .andExpect(jsonPath("$.email").value("posted.user@example.com"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

    }

    @Test
    @Transactional
    void whenTriesCreateUserWithEmptyUsername_thanValidationFail() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"\"," +
                                "\"email\":\"posted.user@example.com\"," +
                                "\"password\":\"Password123\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Minimum length is 6 characters."));

    }

    @Test
    @Transactional
    void whenTriesCreateUserWithShotUsername_thanValidationFail() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"short\"," +
                                "\"email\":\"posted.user@example.com\"," +
                                "\"password\":\"Password123\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Minimum length is 6 characters."));

    }

    @Test
    @Transactional
    void whenTriesCreateUserWithEmptyEmail_thanValidationFail() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"PostedUser\"," +
                                "\"email\":\"\"," +
                                "\"password\":\"Password123\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Email cannot be empty."));

    }

    @Test
    @Transactional
    void whenTriesCreateUserWithoutEmailSymbol_thanValidationFail() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"PostedUser\"," +
                                "\"email\":\"posted.user.example.com\"," +
                                "\"password\":\"Password123\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Email is not valid."));

    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void whenTriesGetUserWithWrongId_andHavePermission_thanThrowEntityNotFoundException() throws Exception {

        mockMvc.perform(get("/user/{userId}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message")
                        .value("Unable to find com.example.monitoring_service.model.ApplicationUser with id 0"));

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser")
    void whenTriesGetUserWithWrongId_andDoesntPermission_thanThrowEntityNotFound() throws Exception {

        mockMvc.perform(get("/user/{userId}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());

    }

    @Test
    @Transactional
    @WithMockUser
    void whenTriesGetInvalidUrlTemplate_thanThrowMethodArgumentTypeMismatchException() throws Exception {

        mockMvc.perform(get("/user/xxx")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; " +
                                "nested exception is java.lang.NumberFormatException: For input string: \"xxx\""));

    }

    @Test
    @Transactional
    void whenTriesCreateUserWithTooLongUsername_thanValidationFail() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" +
                                "TooLongNameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\"," +
                                "\"email\":\"posted.user@example.com\"," +
                                "\"password\":\"Password123\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Maximum length is 255 characters."));

    }

    @Test
    @Transactional
    void whenTriesCreateUserWithNoValidPassword_thenValidationFail() throws Exception {

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"testUser\"," +
                                "\"email\":\"test.user@example.com\"," +
                                "\"password\":\"pass\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value(
                        "Password must be at least 8 characters in length. & " +
                                "Password must contain at least 1 uppercase characters. & " +
                                "Password must contain at least 1 digit characters."
                ));

    }

}
