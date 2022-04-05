package com.example.monitoring_service.controller;

import lombok.Data;

import java.util.Date;

public @Data class MonitoringResultResponse {

    private Date dateOfCheck;
    private int returnedHttpStatusCode;
    private String returnedPayload;

}
