package com.example.bom_spring_boot.worker;

import co.elastic.apm.api.CaptureTransaction;
import com.example.bom_spring_boot.model.RetryableRequest;
import com.example.bom_spring_boot.model.TestWorkerResponse;
import com.example.bom_spring_boot.worker.TestWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class CaseOneWorker extends TestWorker {
    @Override
    @CaptureTransaction
    public Object execute(RetryableRequest request) throws Exception {
        request.setMarketplaceCode("TEST");
        log.info("BEGIN {} execution. Request: {}", this.getClass().getSimpleName(), objectMapper.writeValueAsString(request));
        TestWorkerResponse execute = (TestWorkerResponse) super.execute(request);
        if (Objects.nonNull(execute) && "200".equals(execute.getCode()) && "OK".equalsIgnoreCase(execute.getDescp())) {
            testFunction();
        }

        return execute;
    }

    public void testFunction() throws Exception {
    }
}
