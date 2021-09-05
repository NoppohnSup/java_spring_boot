package com.example.bom_spring_boot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class RetryableServiceConfig {

    @Value("${total-thread-per-pool:10}")
    private int totalThreadPerPool;

    @Bean("retryableExecutor")
    protected ExecutorService executorService() {
        return Executors.newFixedThreadPool(totalThreadPerPool, new CustomizableThreadFactory("retryable-thread-"));
    }
}
