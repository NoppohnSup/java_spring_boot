package com.example.bom_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetryWorkerResponse extends WorkerResponse {
    private Integer code;

    @Override
    public boolean needRetry() {
        return false;
    }
}
