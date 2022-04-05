package com.example.monitoring_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@SuppressWarnings("com.haulmont.jpb.LombokDataInspection")
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public @Data class _Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    public _Base() {
    }

    public _Base(Long id) {
        Id = id;
    }
}
