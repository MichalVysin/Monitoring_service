package com.example.monitoring_service.service;

import com.example.monitoring_service.model.ApplicationUser;
import com.example.monitoring_service.controller.ApplicationUserRequest;
import com.example.monitoring_service.repository.UserRepository;
import com.example.monitoring_service.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ApplicationUser create(ApplicationUserRequest requestedUser) {
        if (userRepository.existsApplicationUserByUsername(requestedUser.getUsername())) {
            throw new EntityExistsException("Username is already taken");
        } else {
            ApplicationUser userToSave = new ApplicationUser(
                    requestedUser.getUsername(),
                    requestedUser.getEmail(),
                    passwordEncoder.encode(requestedUser.getPassword()),
                    UUID.randomUUID().toString(),
                    UserRole.USER
            );

            return userRepository.save(userToSave);
        }
    }

    @Override
    public List<ApplicationUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public ApplicationUser findById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (userRepository.existsApplicationUserByUsername(username)){
            return userRepository.findApplicationUserByUsername(username);
        } else {
            throw new UsernameNotFoundException(username);
        }

    }
}
