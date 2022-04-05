package com.example.monitoring_service.facade;

import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.controller.MonitoredEndpointRequest;

import javax.validation.Valid;
import java.util.List;

public interface MonitoredEndpointFacade {

    List<MonitoredEndpoint> findAllUsersEndpoints(Long userId);

    MonitoredEndpoint getUsersEndpoint(Long userId, Long endpointId);

    MonitoredEndpoint createMonitoredEndpoint(Long userId, @Valid MonitoredEndpointRequest monitoredEndpointRequest);

    void deleteEndpoint(Long userId, Long endpointId);

    void updateEndpoint(Long userId, Long endpointId, @Valid MonitoredEndpointRequest monitoredEndpointRequest);
}
