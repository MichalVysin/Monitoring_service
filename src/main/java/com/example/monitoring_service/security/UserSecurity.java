package com.example.monitoring_service.security;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.model.MonitoredEndpoint;
import com.example.monitoring_service.repository.MonitoredEndpointRepository;
import com.example.monitoring_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserSecurity {

    private final UserRepository userRepository;
    private final MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    public UserSecurity(UserRepository userRepository, MonitoredEndpointRepository monitoredEndpointRepository) {
        this.userRepository = userRepository;
        this.monitoredEndpointRepository = monitoredEndpointRepository;
    }

    public boolean isUser(Authentication authentication, Long pathVariableUserId) {
        String username = authentication.getName();
        ApplicationUser principalUser = userRepository.findApplicationUserByUsername(username);

        if (principalUser != null) {
            return pathVariableUserId == principalUser.getId();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public boolean isUsersEndpoint(Authentication authentication, Long pathVariableUserId, Long pathVariableEndpointId) {
        MonitoredEndpoint monitoredEndpoint = monitoredEndpointRepository.getById(pathVariableEndpointId);
        ApplicationUser user = userRepository.getById(pathVariableUserId);

        ApplicationUser owner = monitoredEndpoint.getUser();

        return isUser(authentication, pathVariableUserId) && user.equals(owner);
    }
}
