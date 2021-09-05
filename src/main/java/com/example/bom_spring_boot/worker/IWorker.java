package com.example.bom_spring_boot.worker;

import com.example.bom_spring_boot.model.RetryableRequest;

public interface IWorker<T,E>
{
    int MAX_RETRY = 3;
    long DELAY = 60000L;
    long MAX_DELAY = 60000L;

    T execute(RetryableRequest request) throws Exception;

    T recover(E exception, RetryableRequest request);
}
