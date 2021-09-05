package com.example.bom_spring_boot.strategy;

import com.example.bom_spring_boot.exception.RetryableException;
import com.example.bom_spring_boot.model.RetryableRequest;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContextRetry {
    @Setter
    private List<Class<? extends RetryStrategy>> strategies;

    @Autowired
    private BeanFactory factory;

    public List<Object> executeStrategy(RetryableException retryableException, RetryableRequest request) {
        for (Class<? extends RetryStrategy> retryStrategy : strategies) {
            RetryStrategy bean = factory.getBean(retryStrategy);
            bean.recover(retryableException, request);
        }
        return null;
    }
}
