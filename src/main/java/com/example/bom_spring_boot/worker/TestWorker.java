package com.example.bom_spring_boot.worker;

import com.example.bom_spring_boot.enums.RetryResponseEnum;
import com.example.bom_spring_boot.model.TestWorkerResponse;
import com.example.bom_spring_boot.model.WorkerResponse;
import com.example.bom_spring_boot.strategy.RecoverEmail;
import com.example.bom_spring_boot.strategy.RetryStrategy;

import java.util.Arrays;
import java.util.List;

public class TestWorker extends Worker {
    @Override
    protected void validateBody(String responseBody) throws Exception {
        if (responseBody.isEmpty() || responseBody.contains(RetryResponseEnum.TIMEOUT_MESSAGE.getDescription()))
            throw new Exception("ResponseBody Invalid.");
    }

    @Override
    protected Class<? extends WorkerResponse> getWorkerResponse() {
        return TestWorkerResponse.class;
    }

    @Override
    protected List<Class<? extends RetryStrategy>> getStrategies() {
        return Arrays.asList(RecoverEmail.class);
    }
}
