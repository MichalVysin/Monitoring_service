package com.example.monitoring_service.service;

import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.model.MonitoringResult;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.MonitoringResultRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NewClassNamingConvention")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MonitoringResultServiceIT {

    @Autowired
    private MonitoringResultService monitoringResultService;

    @Autowired
    private MonitoringResultRepository monitoringResultRepository;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @AfterEach
    void clear(){
        monitoringResultRepository.deleteAll();
        monitoredEndpointRepository.deleteAll();
    }


    @Test
    @Transactional
    void whenCreatesMonitoredEndpointResultWithLongPayload_thenReturnMonitoringEndpointResult() throws InterruptedException {

        monitoredEndpointRepository.save(
                new MonitoredEndpoint("ep", "http://google.com", new ArrayList<>()));

        monitoringResultService.create();

        Thread.sleep(6000L);

        MonitoringResult monitoringResult = new MonitoringResult();

        List<MonitoringResult> monitoringResults = monitoringResultRepository.findAll();
        for (MonitoringResult result : monitoringResults){
            monitoringResult = result;
        }
        assertThat(monitoringResult.getReturnedHttpStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(monitoringResult.getReturnedPayload()).isEqualTo("Payload is too large.");

    }

    @Test
    @Transactional
    void whenCreatesMonitoredEndpointResultWithUnknownUrl_thenReturnMonitoringEndpointResult() throws InterruptedException {

        monitoredEndpointRepository.save(
                new MonitoredEndpoint("ep", "http://http://xxxxxxxxxxxxxxx.com", new ArrayList<>()));

        monitoringResultService.create();

        Thread.sleep(6000L);

        MonitoringResult monitoringResult = new MonitoringResult();

        List<MonitoringResult> monitoringResults = monitoringResultRepository.findAll();
        for (MonitoringResult result : monitoringResults){
            monitoringResult = result;
        }

        assertThat(monitoringResult.getReturnedHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(monitoringResult.getReturnedPayload()).isEqualTo("I/O error on GET request for " +
                "\"http://http/xxxxxxxxxxxxxxx.com\": http; nested exception is java.net.UnknownHostException: http");

    }

    @Test
    @Transactional
    void whenCreatesMonitoredEndpointResultWithBadMapping_thenReturnMonitoringEndpointResult() throws InterruptedException {

        monitoredEndpointRepository.save(
                new MonitoredEndpoint("ep", "http://google.com/000000000000000000000", new ArrayList<>()));

        monitoringResultService.create();

        Thread.sleep(6000L);

        MonitoringResult monitoringResult = new MonitoringResult();

        List<MonitoringResult> monitoringResults = monitoringResultRepository.findAll();
        for (MonitoringResult result : monitoringResults){
            monitoringResult = result;
        }

        assertThat(monitoringResult.getReturnedHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(monitoringResult.getReturnedPayload()).isNotEmpty();

    }
}
