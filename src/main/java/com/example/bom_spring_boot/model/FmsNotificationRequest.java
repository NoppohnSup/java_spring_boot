package com.example.bom_spring_boot.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class FmsNotificationRequest {
    private List<String> actions;
    private Map<String, Object> params;
}
