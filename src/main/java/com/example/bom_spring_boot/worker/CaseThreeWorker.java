package com.example.bom_spring_boot.worker;

import co.elastic.apm.api.CaptureTransaction;
import com.example.bom_spring_boot.model.RetryableRequest;
import com.example.bom_spring_boot.worker.TestWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CaseThreeWorker extends TestWorker {
    @Override
    @CaptureTransaction
    public Object execute(RetryableRequest request) throws Exception {
        log.info("BEGIN {} execution. Request: {}", this.getClass().getSimpleName(), objectMapper.writeValueAsString(request));
        return super.execute(request);
    }
}
