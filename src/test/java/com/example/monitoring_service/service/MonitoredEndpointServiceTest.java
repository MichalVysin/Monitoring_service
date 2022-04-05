package com.example.monitoring_service.service;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.controller.MonitoredEndpointRequest;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MonitoredEndpointServiceTest {

    @Mock
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Mock
    private UserRepository userRepository;

    private MonitoredEndpointService monitoredEndpointService;
    private ApplicationUser user;
    private MonitoredEndpoint endpoint1;
    private MonitoredEndpoint endpoint2;


    @BeforeEach
    void setup(){
        monitoredEndpointService =
                new MonitoredEndpointServiceImpl(monitoredEndpointRepository, userRepository, new ModelMapper());

        user = new ApplicationUser("TestUsername", "test.username@example.com", "Password123");

        endpoint1 = new MonitoredEndpoint("TestEndpoint1", "http://localhost:8080/users");
        endpoint2 = new MonitoredEndpoint("TestEndpoint2", "http://localhost:8080/users");

    }



    @Test
    void whenFoundsAllUsersEndpoints_thanReturnListOfEndpoints() {

        List<MonitoredEndpoint> usersMonitoredEndpoint = new ArrayList<>();
        usersMonitoredEndpoint.add(endpoint1);
        usersMonitoredEndpoint.add(endpoint2);
        user.setMonitoredEndpoints(usersMonitoredEndpoint);

        given(userRepository.getById(user.getId())).willReturn(user);

        List<MonitoredEndpoint> allFoundedUsersEndpoints = monitoredEndpointService.findAllUsersEndpoints(user.getId());

        verify(userRepository).getById(user.getId());

        assertEquals(2, allFoundedUsersEndpoints.size());
        assertEquals(usersMonitoredEndpoint, allFoundedUsersEndpoints);
    }

    @Test
    void whenFindsMonitoredEndpointById_thanReturnEndpoint() {

        given(monitoredEndpointRepository.getById(endpoint1.getId())).willReturn(endpoint1);

        MonitoredEndpoint searchedEndpoint = monitoredEndpointService.findById(endpoint1.getId());

        verify(monitoredEndpointRepository).getById(endpoint1.getId());

        assertEquals(endpoint1, searchedEndpoint);
    }

    @Test
    void whenCreatesMonitoredEndpoint_thenReturnEndpoint() {
        MonitoredEndpointRequest requestedMonitoredEndpoint =
                new MonitoredEndpointRequest("TestEndpoint", "http://localhost:8080/users");


        given(userRepository.existsById(user.getId())).willReturn(true);
        given(userRepository.getById(user.getId())).willReturn(user);

        monitoredEndpointService.create(user.getId(), requestedMonitoredEndpoint);

        ArgumentCaptor<MonitoredEndpoint> monitoredEndpointArgumentCaptor = ArgumentCaptor.forClass(MonitoredEndpoint.class);

        verify(userRepository).getById(user.getId());
        verify(monitoredEndpointRepository).save(monitoredEndpointArgumentCaptor.capture());

        MonitoredEndpoint capturedMonitoredEndpoint = monitoredEndpointArgumentCaptor.getValue();

        assertEquals(capturedMonitoredEndpoint.getName(), requestedMonitoredEndpoint.getName());
        assertEquals(300, capturedMonitoredEndpoint.getMonitoredInterval());
        assertNotNull(capturedMonitoredEndpoint.getDateOfCreation());
        assertNotNull(capturedMonitoredEndpoint.getUser());

    }

    @Test
    void whenDeletesMonitoredEndpointById_thanReturnEndpoint() {

        monitoredEndpointService.deleteById(endpoint1.getId());

        verify(monitoredEndpointRepository).deleteById(endpoint1.getId());

    }

    @Test
    void whenUploadsMonitoredEndpoint_thanReturnEndpoint() {

        MonitoredEndpointRequest requestedMonitoredEndpoint =
                new MonitoredEndpointRequest("editedEndpoint", "http://localhost:8080/users");


        given(monitoredEndpointRepository.getById(endpoint1.getId())).willReturn(endpoint1);

        String endpoint1NameBeforeChange = endpoint1.getName();

        monitoredEndpointService.update(endpoint1.getId(), requestedMonitoredEndpoint);

        ArgumentCaptor<MonitoredEndpoint> monitoredEndpointArgumentCaptor = ArgumentCaptor.forClass(MonitoredEndpoint.class);

        verify(monitoredEndpointRepository).getById(endpoint1.getId());
        verify(monitoredEndpointRepository).saveAndFlush(monitoredEndpointArgumentCaptor.capture());

        MonitoredEndpoint capturedMonitoredEndpoint = monitoredEndpointArgumentCaptor.getValue();

        assertNotEquals(endpoint1NameBeforeChange, capturedMonitoredEndpoint.getName());
        assertEquals(endpoint1.getName(), capturedMonitoredEndpoint.getName());
        assertEquals(requestedMonitoredEndpoint.getName(), capturedMonitoredEndpoint.getName());

    }
}