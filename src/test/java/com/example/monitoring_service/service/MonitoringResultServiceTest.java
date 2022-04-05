package com.example.monitoring_service.service;

import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.model.MonitoringResult;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.MonitoringResultRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MonitoringResultServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;


    @Test
    void whenCreatesMonitoredResult_thanReturnMonitoringResult() {

        MonitoringResultService monitoringResultService =
                new MonitoringResultServiceImpl(monitoringResultRepository, monitoredEndpointRepository, restTemplate);

        MonitoredEndpoint endpoint = new MonitoredEndpoint("TestEndpoint", "http://localhost:8080/users", new ArrayList<>());
        List<MonitoredEndpoint> monitoredEndpoints = new ArrayList<>();
        monitoredEndpoints.add(endpoint);
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);

        given(monitoredEndpointRepository.findAll()).willReturn(monitoredEndpoints);
        given(restTemplate.getForEntity(endpoint.getUrl(), String.class)).willReturn(response);


        monitoringResultService.create();

        verify(monitoredEndpointRepository).findAll();
        verify(restTemplate).getForEntity(endpoint.getUrl(),String.class);
        verify(monitoredEndpointRepository).save(endpoint);


    }


}