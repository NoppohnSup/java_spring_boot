package com.example.bom_spring_boot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetryableResponse {
    private int responseCode;
    private String responseBody;
}
