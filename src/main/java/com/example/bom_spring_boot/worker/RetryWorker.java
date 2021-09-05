package com.example.bom_spring_boot.worker;

import co.elastic.apm.api.CaptureTransaction;
import com.example.bom_spring_boot.model.RetryWorkerResponse;
import com.example.bom_spring_boot.model.RetryableRequest;
import com.example.bom_spring_boot.model.WorkerResponse;
import com.example.bom_spring_boot.strategy.RecoverEmail;
import com.example.bom_spring_boot.strategy.RetryStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RetryWorker extends Worker {

    @Override
    @CaptureTransaction
    public Object execute(RetryableRequest request) throws Exception {
        log.info("BEGIN {} execution. Request: {}", this.getClass().getSimpleName(), objectMapper.writeValueAsString(request));
        return super.execute(request);
    }

    @Override
    protected void validateBody(String responseBody) throws Exception {
        if (StringUtils.isEmpty(responseBody))
            throw new Exception("ResponseBody is Invalid.");
    }

    @Override
    protected Class<? extends WorkerResponse> getWorkerResponse() {
        return RetryWorkerResponse.class;
    }

    @Override
    protected List<Class<? extends RetryStrategy>> getStrategies() {
        return Arrays.asList(RecoverEmail.class);
    }
}
