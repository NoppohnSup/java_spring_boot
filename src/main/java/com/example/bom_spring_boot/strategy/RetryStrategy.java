package com.example.bom_spring_boot.strategy;

import com.example.bom_spring_boot.exception.RetryableException;
import com.example.bom_spring_boot.model.RetryableRequest;

public interface RetryStrategy {
    Object recover(RetryableException retryableException, RetryableRequest request);
}
