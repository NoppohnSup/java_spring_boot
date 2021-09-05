package com.example.bom_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class WorkerResponse {
    public abstract boolean needRetry();
}
