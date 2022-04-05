package com.example.monitoring_service.repository;

import com.example.monitoring_service.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    boolean existsApplicationUserByUsername(String username);

    ApplicationUser findApplicationUserByUsername(String username);

}
