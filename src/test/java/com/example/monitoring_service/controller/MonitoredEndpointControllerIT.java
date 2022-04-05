package com.example.monitoring_service.controller;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NewClassNamingConvention")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MonitoredEndpointControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser user;
    private final List<MonitoredEndpoint> usersEndpoints = new ArrayList<>();
    private MonitoredEndpoint endpoint1;


    @BeforeEach
    void setup() {

        user = new ApplicationUser("TestUser", "test.user@example.com", "Password123", new ArrayList<>());

        endpoint1 = new MonitoredEndpoint("ep1", "http://localhost:8080/users", user);

        usersEndpoints.addAll(Arrays.asList(
                endpoint1,
                new MonitoredEndpoint("ep2", "http://localhost:8080/users", user),
                new MonitoredEndpoint("ep3", "http://localhost:8080/users", user)));

        user.setMonitoredEndpoints(usersEndpoints);

        userRepository.saveAndFlush(user);
        monitoredEndpointRepository.saveAllAndFlush(usersEndpoints);

    }

    @AfterEach
    void clean() {

        monitoredEndpointRepository.deleteAll();
        userRepository.deleteAll();
        usersEndpoints.clear();
    }


    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:read"})
    void whenFindsAllUsersEndpoints_thanReturnListOfMonitoredEndpointResponse() throws Exception {

        mockMvc.perform(get("/user/{userId}/endpoints", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(
                        endpoint1.getName(),
                        "ep2",
                        "ep3"
                )));


    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:read"})
    void whenGetsEndpointById_thenReturnMonitoredEndpointResponse() throws Exception {

        mockMvc.perform(get("/user/{userId}/endpoint/{endpointId}", user.getId(), endpoint1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(endpoint1.getName()))
                .andExpect(jsonPath("$.url").value(endpoint1.getUrl()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenCreatesEndpoint_thenReturnMonitoredEndpointResponse() throws Exception {

        mockMvc.perform(post("/user/{userId}/endpoint", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"endpoint\",\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("endpoint"))
                .andExpect(jsonPath("$.url").value("http://localhost:8080/users"))
                .andExpect(jsonPath("$.dateOfCreation").isNotEmpty())
                .andExpect(jsonPath("$.monitoredInterval").value(300));

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenDeletesMonitoredEndpoint_thenCorrect() throws Exception {

        mockMvc.perform(delete("/user/{userId}/endpoint/{endpointId}/delete", user.getId(), endpoint1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        boolean isEndpointExist = monitoredEndpointRepository.existsById(endpoint1.getId());

        assertThat(isEndpointExist).isFalse();

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenEditsExistingMonitoredEndpoint_thanCorrect() throws Exception {


        MonitoredEndpoint existingMonitoredEndpoint = endpoint1;

        mockMvc.perform(put("/user/{userId}/endpoint/{endpointId}/edit", user.getId(), existingMonitoredEndpoint.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"editedName\",\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isOk());

        MonitoredEndpoint editedMonitoredEndpoint = monitoredEndpointRepository.getById(existingMonitoredEndpoint.getId());

        assertThat(editedMonitoredEndpoint.getName()).isEqualTo("editedName");
        assertThat(editedMonitoredEndpoint.getId()).isEqualTo(existingMonitoredEndpoint.getId());
        assertThat(editedMonitoredEndpoint.getUser()).isEqualTo(existingMonitoredEndpoint.getUser());
        assertThat(editedMonitoredEndpoint.getDateOfCreation()).isEqualTo(existingMonitoredEndpoint.getDateOfCreation());

       }


    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesCreateEndpointWithEmptyName_thanValidationFail() throws Exception {

        mockMvc.perform(post("/user/{userId}/endpoint", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Name cannot be empty."));
    }


    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesCreateEndpointWithEmptyUrl_thanValidationFail() throws Exception {

        mockMvc.perform(post("/user/{userId}/endpoint", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"endpoint\",\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Url cannot be empty."));

  }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesCreateEndpointWithNotValidUrl_thanValidationFail() throws Exception {

        mockMvc.perform(post("/user/{userId}/endpoint", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"endpoint\",\"url\":\"thisIsNotValidUrl\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Url is not valid."));

 }

    @Test
    @Transactional
    void whenTriesGetEndpointWithWrongId_andDoesntHaveAuthority_thenThrowForbidden() throws Exception {

        ApplicationUser user = userRepository.save(
                new ApplicationUser("TestUser", "test.user@example.com", "Password123", new ArrayList<>()));

        mockMvc.perform(get("/user/{userId}/endpoint/{endpointId}", user.getId(), 0L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        monitoredEndpointRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:read"})
    void whenTriesGetEndpointWithWrongId_andHasAuthority_thenEntityNotFoundException() throws Exception {

        mockMvc.perform(get("/user/{userId}/endpoint/{endpointId}", user.getId(), 0L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message")
                        .value("Unable to find com.example.monitoring_service.model.MonitoredEndpoint with id 0"));

  }


    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:read"})
    void whenTriesGetUsersEndpointsByWrongId_thenThrowForbidden() throws Exception {

        ApplicationUser userTwo = userRepository.save(
                new ApplicationUser("TestUserTwo", "test.user@example.com", "Password123", new ArrayList<>()));


        mockMvc.perform(get("/user/{userId}/endpoints", userTwo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }


    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:read"})
    void whenTriesGetInvalidUrlTemplate_thanMethodArgumentTypeMismatchException() throws Exception {

        mockMvc.perform(get("/user/{userId}/endpoint/xxx", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; " +
                                "nested exception is java.lang.NumberFormatException: For input string: \"xxx\""));

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesDeleteEndpointByNonExistenceId_thenEmptyResultDataAccessException() throws Exception {

        mockMvc.perform(delete("/user/{userId}/endpoint/{id}/delete", user.getId(), 0L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message")
                        .value("Unable to find com.example.monitoring_service.model.MonitoredEndpoint with id 0"));

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesUploadEndpointWithEmptyName_thenValidationFail() throws Exception {

        mockMvc.perform(put("/user/{userId}/endpoint/{endpointId}/edit", user.getId(), endpoint1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Name cannot be empty."));

   }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesUploadEndpointWithEmptyUrl_thenValidationFail() throws Exception {

        mockMvc.perform(put("/user/{userId}/endpoint/{endpointId}/edit", user.getId(), endpoint1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"editedEndpoint\",\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Url cannot be empty."));

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesUploadEndpointWithInvalidUrl_thenValidationFail() throws Exception {

        mockMvc.perform(put("/user/{userId}/endpoint/{endpointId}/edit", user.getId(), endpoint1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"editedEndpoint\",\"url\":\"thisIsNotValidUrl\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Url is not valid."));

    }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesUploadEndpointNonExistenceId_thenEntityNotFoundException() throws Exception {

        mockMvc.perform(put("/user/{userId}/endpoint/{endpointId}/edit", user.getId(), 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"editedEndpoint\",\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("Unable to find com.example.monitoring_service.model.MonitoredEndpoint with id 0"));

       }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesCreateEndpointWithTooLongName_thanValidationFail() throws Exception {

        mockMvc.perform(post("/user/{userId}/endpoint", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"endpoint\"," +
                                "\"url\":\"http://TooLongUrlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll" +
                                "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll" +
                                "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Maximum length is 255 characters."));

      }


    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesUploadEndpointWithLongUrl_thenValidationFail() throws Exception {

        mockMvc.perform(post("/user/{userId}/endpoint", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"TooLongNameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                                "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\"," +
                                "\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields[*].message").value("Maximum length is 255 characters."));

      }

    @Test
    @Transactional
    @WithMockUser(username = "TestUser", authorities = {"endpoint:write"})
    void whenTriesCreateEndpointAndUserWasNotFound_thenThrowForbidden() throws Exception {

        long id = 0;

        mockMvc.perform(post("/user/{userId}/endpoint", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"endpoint\",\"url\":\"http://localhost:8080/users\"}"))
                .andExpect(status().isForbidden());

    }

}