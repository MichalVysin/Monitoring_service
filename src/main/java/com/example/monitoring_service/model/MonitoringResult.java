package com.example.monitoring_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "monitoring_results")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public @Data class MonitoringResult extends _Base {

    @Column(name = "date_of_check")
    private Date dateOfCheck;
    @Column(name = "status_code")
    private int returnedHttpStatusCode;
    @Column(name = "payload")
    private String returnedPayload;
    @ManyToOne
    @JoinColumn(name = "endpoint_id")
    private MonitoredEndpoint monitoredEndpoint;

    public MonitoringResult() {
    }

    public MonitoringResult(MonitoredEndpoint monitoredEndpoint) {
        this.monitoredEndpoint = monitoredEndpoint;
    }
}
