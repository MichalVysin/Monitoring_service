package com.example.monitoring_service.facade;

import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.controller.MonitoredEndpointRequest;
import com.example.monitoring_service.service.MonitoredEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class MonitoredEndpointFacadeImpl implements MonitoredEndpointFacade {

    private final MonitoredEndpointService monitoredEndpointService;

    @Autowired
    public MonitoredEndpointFacadeImpl(MonitoredEndpointService monitoredEndpointService) {
        this.monitoredEndpointService = monitoredEndpointService;
    }

    @Override
    @PreAuthorize("hasAuthority('endpoint:read') and @userSecurity.isUser(authentication, #userId)")
    public List<MonitoredEndpoint> findAllUsersEndpoints(Long userId){
        return monitoredEndpointService.findAllUsersEndpoints(userId);
    }

    @Override
    @PreAuthorize("hasAuthority('endpoint:read') and @userSecurity.isUser(authentication, #userId)")
    public MonitoredEndpoint getUsersEndpoint(Long userId, Long endpointId){
        return monitoredEndpointService.findById(endpointId);
    }

    @Override
    @PreAuthorize("hasAuthority('endpoint:write') and @userSecurity.isUser(authentication, #userId)")
    public MonitoredEndpoint createMonitoredEndpoint(Long userId, @Valid MonitoredEndpointRequest monitoredEndpointRequest){
        return monitoredEndpointService.create(userId, monitoredEndpointRequest);
    }

    @Override
    @PreAuthorize("hasAuthority('endpoint:write') and @userSecurity.isUsersEndpoint(authentication, #userId, #endpointId)")
    public void deleteEndpoint(Long userId, Long endpointId){
        monitoredEndpointService.deleteById(endpointId);
    }

    @Override
    @PreAuthorize("hasAuthority('endpoint:write') and @userSecurity.isUsersEndpoint(authentication, #userId, #endpointId)")
    public void updateEndpoint(Long userId, Long endpointId, @Valid MonitoredEndpointRequest monitoredEndpointRequest){
        monitoredEndpointService.update(endpointId, monitoredEndpointRequest);
    }

}
