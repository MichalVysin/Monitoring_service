package com.example.monitoring_service.service;

import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.controller.MonitoredEndpointRequest;

import java.util.List;

public interface MonitoredEndpointService {

    List<MonitoredEndpoint> findAllUsersEndpoints(Long userId);

    MonitoredEndpoint findById(Long id);

    MonitoredEndpoint create(Long userId, MonitoredEndpointRequest monitoredEndpointRequest);

    void deleteById(Long id);

    void update(Long id, MonitoredEndpointRequest monitoredEndpointRequest);
}
