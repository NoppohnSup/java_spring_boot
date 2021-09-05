package com.example.bom_spring_boot.http;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HttpRequest {
    private String url;
    private String jsonBody;
    private Map<String, String> queryString;
    private Map<String, String> header;
}
