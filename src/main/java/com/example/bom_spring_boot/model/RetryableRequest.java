package com.example.bom_spring_boot.model;

import lombok.Data;

import java.util.Map;

@Data
public class RetryableRequest
{
    private String worker;

    private String marketplaceCode;

    private String url;

    private String method;

    private boolean async = true;

    private Map<String, String> headers;

    private Object body;

    private String createdBy;
}
