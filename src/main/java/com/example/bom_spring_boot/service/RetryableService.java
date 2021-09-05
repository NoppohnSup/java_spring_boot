package com.example.bom_spring_boot.service;

import com.example.bom_spring_boot.mdc.MdcCallable;
import com.example.bom_spring_boot.model.RetryableRequest;
import com.example.bom_spring_boot.worker.IWorker;
import com.example.bom_spring_boot.worker.WorkerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class RetryableService {
    private final BeanFactory beanFactory;

    private final ExecutorService executor;

    private Map<String, Object> response = new HashMap<>();

    // To wait until thread finish without timeout, please set it to 0
    @Value("${retry-synchronous-timeout:180}")
    private int threadTimeout;

    @Autowired
    public RetryableService(BeanFactory beanFactory, ExecutorService executor) {
        this.beanFactory = beanFactory;
        this.executor = executor;
    }

    public Object retry(RetryableRequest retryableRequest) throws Exception {
        String workerName = retryableRequest.getWorker();
        IWorker worker = WorkerFactory.from(beanFactory, workerName);

        Future<Object> future = executor.submit(new MdcCallable<>(() -> {
            try {
                return worker.execute(retryableRequest);
            } catch (Throwable t) {
                log.error("Execute worker in MdcCallable error {}: {}", t.getClass(), t.getMessage());
                throw t;
            }
        }));

        // For synchronous process, call .get() to wait for result
        if (!retryableRequest.isAsync()) {
            if (threadTimeout > 0) {
                try {
                    // wait at most threadTimeout
                    return future.get(threadTimeout, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    log.error("Worker could not finish process within {} seconds", threadTimeout);
                }
            } else {
                // wait until thread finish
                return future.get();
            }
        }

        return response;
    }
}
