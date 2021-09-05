package com.example.bom_spring_boot.http;

import com.example.bom_spring_boot.exception.HttpException;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class HttpService
{
    @Autowired
    private HttpClient httpClient;

    public HttpResponse get(HttpRequest request) throws Exception {
        String url = request.getUrl();
        try {
            Map<String, String> header = request.getHeader() != null ? request.getHeader() : new HashMap<>();
            Map<String, String> queryString = request.getQueryString() != null ? request.getQueryString() : new HashMap<>();

            URIBuilder builder = new URIBuilder(url);
            queryString.forEach(builder::addParameter);

            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
            if (!header.isEmpty()) {
                header.forEach(httpGet::setHeader);
            }

            return httpClient.execute(httpGet);
        } catch (Exception e) {
            log.error("SERVICE  HttpService.get EXCEPTION MESSAGE {}", Throwables.getStackTraceAsString(e));
            throw new HttpException(String.format("Error: Cannot make GET HttpRequest to %s", url));
        }
    }

    public HttpResponse post(HttpRequest request) throws Exception {
        String url = request.getUrl();
        try {
            log.info("Send POST with HttpRequest: {}", request);
            
            String jsonBody = request.getJsonBody();
            Map<String, String> header = request.getHeader() != null ? request.getHeader() : new HashMap<>();

            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);

            if (!header.isEmpty()) {
                header.forEach(httpPost::setHeader);
            }

            return httpClient.execute(httpPost);
        } catch (Exception e) {
            log.error("SERVICE  HttpService.post EXCEPTION MESSAGE {}", Throwables.getStackTraceAsString(e));
            throw new HttpException(String.format("Error: Cannot make POST HttpRequest to %s", url));
        }
    }
}
