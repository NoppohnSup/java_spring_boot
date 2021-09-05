package com.example.bom_spring_boot.service;

import co.elastic.apm.api.CaptureTransaction;
import com.example.bom_spring_boot.Util.RestUtil;
import com.example.bom_spring_boot.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.naming.ConfigurationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ThirdPartyService {

    static final String ACTION_TYPE = "sync_stock";

    @Autowired
    ClientService clientService;

    @Autowired
    protected ObjectMapper objectMapper;

    @CaptureTransaction
    public void handleSyncStockToClient(Event event) {
        List<Map<String, Object>> eventData = (List<Map<String, Object>>) event.getData();
        Map<String, List<String>> buSkuList = eventData.stream()
                .filter(i -> !Optional.ofNullable(i.get("client_name")).orElse("").toString().isEmpty())
                .filter(i -> !Optional.ofNullable(i.get("sku")).orElse("").toString().isEmpty())
                .collect(Collectors.groupingBy(i -> i.get("client_name").toString(), Collectors.mapping(i -> i.get("sku").toString(), Collectors.toList())));

        String endpoint = "";
        String method = "";
        for (Map.Entry<String, List<String>> buSku : buSkuList.entrySet()) {
            List<String> jsonRequestList = new ArrayList<>();
            try {
                Map<String, Object> buConfig = clientService.getBuConfig(buSku.getKey(), event.getAction(), ACTION_TYPE);
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("client_name", buSku.getKey());
                jsonMap.put("sku", buSku.getValue());
                jsonMap.put("action", event.getAction());
                jsonRequestList = Arrays.asList(new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                        .toJson(jsonMap));
                endpoint = buConfig.get("endpoint").toString();
                method = buConfig.get("method").toString();

                Map<String, Object> authentication = (Map<String, Object>) Optional.ofNullable(buConfig.get("authen")).orElse(new HashMap<>());
                String authUrl = Optional.ofNullable(authentication.get("url")).orElse("").toString();

                Map<String, Object> authRequestMap = new HashMap();
                authRequestMap.put("grant_type", Optional.ofNullable(authentication.get("grant_type")).orElse("").toString());
                authRequestMap.put("client_id", Optional.ofNullable(authentication.get("client_id")).orElse("").toString());
                authRequestMap.put("client_secret", Optional.ofNullable(authentication.get("client_secret")).orElse("").toString());

                for (String json : jsonRequestList) {
                    try {
                        String token = RestUtil.wlsToken(authUrl, authRequestMap);
                        Map<String, String> accessToken = new HashMap<>();
                        accessToken.put("X-WLS-Access-Token", token);

                        String response = RestUtil.callHttpPut(json, endpoint, accessToken);
                        JSONObject responseJson = new JSONObject(response);

                        if (!"success".equalsIgnoreCase(responseJson.get("status").toString())) {
                            log.warn("Response header 200 : status = error : " + method + " : " + endpoint + " response : " + responseJson.toString());
                        }

                        Thread.sleep(500);

                    } catch (HttpClientErrorException e) {
                        log.error("HttpClientErrorException : Request FAILED url : " + endpoint + " method : " + method + " message : " + e.getStatusCode() + ", " + e.getResponseBodyAsString());
                    }
                }
            } catch (ConfigurationException e) {
                log.error("ConfigurationException : Please check your config. : " + e.getMessage());
            } catch (Exception e) {
                log.error("Exception : Request FAILED url : " + endpoint + " method : " + method + " message : " + e.getMessage());
            }
        }
    }
}
