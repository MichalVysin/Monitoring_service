package com.example.monitoring_service.controller;

import lombok.Data;

import java.util.Date;
import java.util.List;

public @Data class MonitoredEndpointResponse {

    private String name;
    private String url;
    private Date dateOfCreation;
    private Date dateOfLastCheck;
    private int monitoredInterval;
    private List<MonitoringResultResponse> monitoringResults;
    private ApplicationUserResponse user;


}
