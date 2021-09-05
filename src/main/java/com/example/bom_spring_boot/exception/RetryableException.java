package com.example.bom_spring_boot.exception;

import com.example.bom_spring_boot.model.RetryableResponse;
import lombok.Data;

@Data
public class RetryableException extends Exception {
    private RetryableResponse retryableResponse;

    public RetryableException(Exception e, RetryableResponse retryableResponse) {
        super(e);

        this.retryableResponse = retryableResponse;
    }

    public RetryableException(RetryableResponse retryableResponse) {
        this.retryableResponse = retryableResponse;
    }
}
