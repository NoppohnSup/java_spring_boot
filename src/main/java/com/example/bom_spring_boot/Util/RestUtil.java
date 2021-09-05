package com.example.bom_spring_boot.Util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestUtil {

    public static ResponseEntity call(String jsonEvent, String url, String method) {
        log.info("Request to : " + url + " Method : " + method + " Request body : " + jsonEvent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonEvent, headers);
        HttpMethod httpMethod;

        switch (method.toUpperCase()) {
            case "PUT" :
                httpMethod = HttpMethod.PUT;
                break;
            case "POST" :
            default:
                httpMethod = HttpMethod.POST;
                break;
        }

        return new RestTemplate().exchange(url, httpMethod, requestEntity, String.class);
    }

    public static ResponseEntity call(String jsonEvent, String url, String method, Map<String, String> accessToken) {
        log.info("Request to : " + url + " Method : " + method + " Request body : " + jsonEvent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (!accessToken.isEmpty()) {
            for (Map.Entry<String, String> headerParam : accessToken.entrySet()) {
                headers.set(headerParam.getKey(), headerParam.getValue());
            }
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonEvent, headers);
        HttpMethod httpMethod;

        switch (method.toUpperCase()) {
            case "PUT" :
                httpMethod = HttpMethod.PUT;
                break;
            case "POST" :
            default:
                httpMethod = HttpMethod.POST;
                break;
        }

        return new RestTemplate().exchange(url, httpMethod, requestEntity, String.class);
    }

    public static String callHttpPut(String jsonEvent, String url, Map<String, String> accessToken) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);

        if (!accessToken.isEmpty()) {
            for (Map.Entry<String, String> headerParam : accessToken.entrySet()) {
                httpPut.setHeader(headerParam.getKey(), headerParam.getValue());
            }
        }

        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        StringEntity stringEntity = new StringEntity(jsonEvent, ContentType.APPLICATION_JSON);
        httpPut.setEntity(stringEntity);
        HttpResponse resp = httpclient.execute(httpPut);

        String result = EntityUtils.toString(resp.getEntity());
        log.info("RESPONSE from sync stock to Merchant URL: {}, REQUEST: {}, RESPONSE: {}", url, jsonEvent, result);

        if (result.isEmpty()) {
            throw new Exception(url + ": response is empty !!!");
        }
        return result;
    }

    public static String wlsToken(String authUrl, Map<String, Object> authRequestMap) throws Exception {
        try {
            Map<String, String> header = new HashMap<>();
            String result;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(authUrl);
            if (!header.isEmpty()) {
                for (Map.Entry<String, String> headerParam : header.entrySet()) {
                    httppost.setHeader(headerParam.getKey(), headerParam.getValue());
                }
            }

            List request = new ArrayList();
            for (Map.Entry<String, Object> requestParam : authRequestMap.entrySet()) {
                request.add(new BasicNameValuePair(requestParam.getKey(), String.valueOf(requestParam.getValue())));
            }

            httppost.setEntity(new UrlEncodedFormEntity(request));
            HttpResponse resp = httpclient.execute(httppost);
            result = EntityUtils.toString(resp.getEntity());
            log.info("RESPONSE from get WLS token: {} REQUEST: {} RESPONSE: {}", authUrl, authRequestMap, result);
            if (result.isEmpty()) {
                throw new Exception("response from get token MC is empty !!!");
            }
            JSONObject response = new JSONObject(result);
            return response.get("access_token").toString();

        } catch (HttpClientErrorException e) {
            log.error("wlsToken HttpClientErrorException: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("wlsToken Exception: {}", e.getMessage(), e);
            throw new Exception("Cannot get 'access_token' from Merchant.");
        }
    }


}
