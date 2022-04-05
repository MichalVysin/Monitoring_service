package com.example.monitoring_service.service;

import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.model.MonitoringResult;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class MonitoringResultServiceImpl implements MonitoringResultService {

    private final MonitoringResultRepository monitoringResultRepository;
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final RestTemplate restTemplate;


    @Autowired
    public MonitoringResultServiceImpl(MonitoringResultRepository monitoringResultRepository,
                                       MonitoredEndpointRepository monitoredEndpointRepository,
                                       RestTemplate restTemplate) {
        this.monitoringResultRepository = monitoringResultRepository;
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void create() {
        List<MonitoredEndpoint> monitoredEndpoints = monitoredEndpointRepository.findAll();

        for (MonitoredEndpoint mE : monitoredEndpoints) {

            MonitoringResult monitoringResult = new MonitoringResult();
            Date checkDate = new Date();

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(mE.getUrl(), String.class);

                String payload = response.getBody();

                monitoringResult.setReturnedHttpStatusCode(response.getStatusCodeValue());

                if (payload != null && payload.length() < 255) {
                    monitoringResult.setReturnedPayload(payload);
                } else {
                    monitoringResult.setReturnedPayload("Payload is too large.");
                }

            } catch (HttpClientErrorException e) {
                monitoringResult.setReturnedHttpStatusCode(e.getStatusCode().value());
                if (e.getResponseBodyAsString().length() < 255) {
                    monitoringResult.setReturnedPayload(e.getResponseBodyAsString());
                } else  {
                    monitoringResult.setReturnedPayload(e.getResponseBodyAsString().substring(0,240) + "<<<abbreviated");
                }
            } catch (ResourceAccessException e) {
                if (e.getMessage() != null && e.getMessage().length() < 255){
                    monitoringResult.setReturnedPayload(e.getMessage());

                } else {
                    monitoringResult.setReturnedPayload(e.getMessage().substring(0,240) + "<<<abbreviated");
                }
                monitoringResult.setReturnedHttpStatusCode(HttpStatus.NOT_FOUND.value());
            }

            monitoringResult.setMonitoredEndpoint(mE);
            monitoringResult.setDateOfCheck(checkDate);


            mE.addResult(monitoringResult);
            mE.setDateOfLastCheck(checkDate);

            monitoringResultRepository.save(monitoringResult);
            monitoredEndpointRepository.save(mE);
        }
    }
}
