package com.example.monitoring_service.controller;

import com.example.monitoring_service.facade.MonitoredEndpointFacade;
import com.example.monitoring_service.model.MonitoredEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MonitoredEndpointController {

    private final MonitoredEndpointFacade monitoredEndpointFacade;
    private final ModelMapper modelMapper;

    @Autowired
    public MonitoredEndpointController(MonitoredEndpointFacade monitoredEndpointFacade, ModelMapper modelMapper) {
        this.monitoredEndpointFacade = monitoredEndpointFacade;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/user/{userId}/endpoints")
    @ResponseStatus(HttpStatus.OK)
    public List<MonitoredEndpointResponse> findAllUsersEndpoints(@PathVariable Long userId) {
        List<MonitoredEndpoint> monitoredEndpoints = monitoredEndpointFacade.findAllUsersEndpoints(userId);
        return monitoredEndpoints
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/user/{userId}/endpoint/{endpointId}")
    @ResponseStatus(HttpStatus.OK)
    public MonitoredEndpointResponse getEndpoint(@PathVariable Long userId,
                                                 @PathVariable Long endpointId) {
        return convertToResponse(monitoredEndpointFacade.getUsersEndpoint(userId, endpointId));
    }


    @PostMapping(value = "/user/{userId}/endpoint")
    @ResponseStatus(HttpStatus.CREATED)
    public MonitoredEndpointResponse createEndpoint(@PathVariable Long userId,
                                                    @RequestBody MonitoredEndpointRequest monitoredEndpointRequest) {
        return convertToResponse(monitoredEndpointFacade.createMonitoredEndpoint(userId, monitoredEndpointRequest));
    }

    @DeleteMapping(value = "/user/{userId}/endpoint/{endpointId}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long endpointId){
        monitoredEndpointFacade.deleteEndpoint(userId, endpointId);
    }


    @PutMapping(value = "/user/{userId}/endpoint/{endpointId}/edit")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long userId,
                       @PathVariable Long endpointId,
                       @RequestBody MonitoredEndpointRequest monitoredEndpointRequest){
        monitoredEndpointFacade.updateEndpoint(userId, endpointId, monitoredEndpointRequest);
    }


    private MonitoredEndpointResponse convertToResponse(MonitoredEndpoint monitoredEndpoint){
        return modelMapper.map(monitoredEndpoint, MonitoredEndpointResponse.class);
    }
}
