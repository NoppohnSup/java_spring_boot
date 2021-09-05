package com.example.bom_spring_boot.strategy;

import com.example.bom_spring_boot.exception.RetryableException;
import com.example.bom_spring_boot.http.HttpRequest;
import com.example.bom_spring_boot.http.HttpService;
import com.example.bom_spring_boot.model.FmsNotificationRequest;
import com.example.bom_spring_boot.model.RetryableRequest;
import com.example.bom_spring_boot.model.RetryableResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RecoverEmail implements RetryStrategy {
    private static final String RETRYABLE_RECOVER_ACTION = "retryable_recover";

    @Autowired
    private HttpService httpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Object recover(RetryableException retryableException, RetryableRequest request) {
        String requestJson = StringUtils.EMPTY;

        try {
            requestJson = objectMapper.writeValueAsString(request);
            RetryableResponse retryableResponse = retryableException.getRetryableResponse();
            Map<String, Object> paramsMap = new HashMap<>();

            Map<String, String> header = request.getHeaders() != null ? request.getHeaders() : new HashMap<>();
            paramsMap.put("worker", request.getWorker());
            paramsMap.put("url", request.getUrl());
            paramsMap.put("request_headers", objectMapper.writeValueAsString(header));
            paramsMap.put("request_body", objectMapper.writeValueAsString(request.getBody()));
            paramsMap.put("response_code", String.valueOf(retryableResponse.getResponseCode()));
            paramsMap.put("response_body", retryableResponse.getResponseBody());

            FmsNotificationRequest notificationRequest = FmsNotificationRequest.builder()
                .actions(Arrays.asList(RETRYABLE_RECOVER_ACTION))
                .params(paramsMap)
                .build();

            String jsonBody = objectMapper.writeValueAsString(notificationRequest);
            HttpRequest httpRequest = HttpRequest.builder()
                .url("api-send-mail")
                .jsonBody(jsonBody)
                .build();

            HttpResponse response = httpService.post(httpRequest);
            String responseBody = EntityUtils.toString(response.getEntity());
            log.info("Log recover() request: {}", requestJson);
            log.info("Log recover() responseBody: {}", responseBody);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new Exception("Cannot send notification.");
            }

            return responseBody;
        } catch (Exception e) {
            log.error("Log recover() request: {}", requestJson);
            log.error("Exception recover() message: {}", e.getMessage());
        }

        return null;
    }
}
