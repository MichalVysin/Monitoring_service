package com.example.monitoring_service.service;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.controller.MonitoredEndpointRequest;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class MonitoredEndpointServiceImpl implements MonitoredEndpointService {

    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MonitoredEndpointServiceImpl(MonitoredEndpointRepository monitoredEndpointRepository,
                                        UserRepository userRepository,
                                        ModelMapper modelMapper) {
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MonitoredEndpoint> findAllUsersEndpoints(Long userId) {
        ApplicationUser user = userRepository.getById(userId);
        return user.getMonitoredEndpoints();
    }

    @Override
    public MonitoredEndpoint findById(Long id) {
        return monitoredEndpointRepository.getById(id);
    }

    @Override
    @Transactional
    public MonitoredEndpoint create(Long userId, MonitoredEndpointRequest monitoredEndpointRequest) {

        if (userRepository.existsById(userId)) {
            ApplicationUser user = userRepository.getById(userId);

            MonitoredEndpoint requestedConvertedMonitoredEndpoint = convertToEntity(monitoredEndpointRequest);
            requestedConvertedMonitoredEndpoint.setDateOfCreation(new Date());
            requestedConvertedMonitoredEndpoint.setMonitoredInterval(300);
            requestedConvertedMonitoredEndpoint.setUser(user);

            return monitoredEndpointRepository.save(requestedConvertedMonitoredEndpoint);
        } else {
            throw new EntityNotFoundException(String.format("User %d not found.", userId));
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        monitoredEndpointRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(Long id, MonitoredEndpointRequest monitoredEndpointRequest) {
        MonitoredEndpoint existingMonitoredEndpoint = monitoredEndpointRepository.getById(id);
        BeanUtils.copyProperties(convertToEntity(monitoredEndpointRequest), existingMonitoredEndpoint, "id", "dateOfCreation", "dateOfLastCheck", "monitoredInterval", "monitoringResults", "user");
        monitoredEndpointRepository.saveAndFlush(existingMonitoredEndpoint);
    }


    private MonitoredEndpoint convertToEntity(MonitoredEndpointRequest monitoredEndpointRequest) {
        return modelMapper.map(monitoredEndpointRequest, MonitoredEndpoint.class);
    }

}
