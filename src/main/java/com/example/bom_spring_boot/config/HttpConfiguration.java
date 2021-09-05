package com.example.bom_spring_boot.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;


@Configuration
public class HttpConfiguration {

    // To use default RequestConfig (no timeout), please set it to -1
    @Value("${http-timeout:60}")
    private Integer httpTimeout;

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "prototype")
    public HttpClient getHttpClient() {
        return HttpClientBuilder.create()
            .setDefaultRequestConfig(createRequestConfig())
            .build();
    }

    private RequestConfig createRequestConfig() {
        return (httpTimeout == null || httpTimeout < 0) ? RequestConfig.DEFAULT :
            RequestConfig.custom()
                .setConnectTimeout(httpTimeout * 1000)
                .setConnectionRequestTimeout(httpTimeout * 1000)
                .setSocketTimeout(httpTimeout * 1000)
                .build();
    }
}
