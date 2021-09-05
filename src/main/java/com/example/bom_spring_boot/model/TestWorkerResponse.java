package com.example.bom_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestWorkerResponse extends WorkerResponse{
    private String code;
    private String descp;

    public boolean needRetry() {
        String descpForCheck =  Optional.ofNullable(descp).orElse("").toUpperCase();
        return !(descpForCheck.contains("BEEN CONFIRM") ||
                 descpForCheck.contains("BEEN CANCEL") ||
                 descpForCheck.contains("DUPLICATE") ||
                 descpForCheck.contains("OK"));
    }
}