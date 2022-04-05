package com.example.monitoring_service.handling_errors;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data class ErrorResponse {

    private List<ErrorField> errorFields = new ArrayList<>();
}

