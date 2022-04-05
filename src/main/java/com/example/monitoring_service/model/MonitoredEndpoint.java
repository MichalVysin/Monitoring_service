package com.example.monitoring_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "monitored_endpoints")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public @Data class MonitoredEndpoint extends _Base {


    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "date_of_creation")
    private Date dateOfCreation;

    @Column(name = "date_of_last_check")
    private Date dateOfLastCheck;

    @Column(name = "monitored_interval")
    private int monitoredInterval;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "monitoredEndpoint", orphanRemoval = true)
    private List<MonitoringResult> monitoringResults;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    public MonitoredEndpoint() {
    }

    public MonitoredEndpoint(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public MonitoredEndpoint(String name, String url, List<MonitoringResult> monitoringResults) {
        this.name = name;
        this.url = url;
        this.monitoringResults = monitoringResults;
    }

    public MonitoredEndpoint(String name, String url, ApplicationUser user) {
        this.name = name;
        this.url = url;
        this.user = user;
    }

    public MonitoredEndpoint(String name, String url, List<MonitoringResult> monitoringResults, ApplicationUser user) {
        this.name = name;
        this.url = url;
        this.monitoringResults = monitoringResults;
        this.user = user;
    }

    public void addResult(MonitoringResult monitoringResult){
        monitoringResults.add(monitoringResult);
    }
}
