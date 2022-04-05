package com.example.monitoring_service.repository;

import com.example.monitoring_service.model.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {
}
