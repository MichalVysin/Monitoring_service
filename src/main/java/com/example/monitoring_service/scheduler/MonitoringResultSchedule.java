package com.example.monitoring_service.scheduler;

import com.example.monitoring_service.service.MonitoringResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class MonitoringResultSchedule {

    private final MonitoringResultService monitoringResultService;

    @Autowired
    public MonitoringResultSchedule(MonitoringResultService monitoringResultService) {
        this.monitoringResultService = monitoringResultService;
    }


    @Scheduled(fixedDelay = 1 * 5 * 1000)
    public void doMonitoredResult() {

        monitoringResultService.create();

    }


}
