package com.example.bom_spring_boot.worker;

import com.example.bom_spring_boot.exception.RetryableException;
import com.example.bom_spring_boot.http.HttpRequest;
import com.example.bom_spring_boot.http.HttpService;
import com.example.bom_spring_boot.model.RetryableRequest;
import com.example.bom_spring_boot.model.RetryableResponse;
import com.example.bom_spring_boot.model.WorkerResponse;
import com.example.bom_spring_boot.strategy.ContextRetry;
import com.example.bom_spring_boot.strategy.RetryStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public abstract class Worker implements IWorker<Object, RetryableException> {
    @Autowired
    private HttpService httpService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ContextRetry contextRetry;

    @Retryable(
        value = Exception.class,
        maxAttempts = MAX_RETRY,
        backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY)
    )
    @Override
    public Object execute(RetryableRequest request) throws Exception {
        return send(request);
    }

    protected Object send(RetryableRequest request) throws Exception {
        double startExec = System.currentTimeMillis();
        String requestBody = StringUtils.EMPTY;
        String responseBody = StringUtils.EMPTY;
        Integer statusCode = NumberUtils.INTEGER_ZERO;
        String exceptionMsg = StringUtils.EMPTY;
        RetryableResponse retryableResponse = null;

        try {
            requestBody = objectMapper.writeValueAsString(request.getBody());

            HttpRequest httpRequest = HttpRequest.builder()
                .url(request.getUrl())
                .header(request.getHeaders())
                .jsonBody(requestBody)
                .build();

            HttpResponse response = httpService.post(httpRequest);
            responseBody = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            statusCode = response.getStatusLine().getStatusCode();

            retryableResponse = RetryableResponse.builder()
                .responseCode(statusCode)
                .responseBody(responseBody)
                .build();

            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("StatusCode != 200");
            }

            validateBody(responseBody);

            WorkerResponse workerResponse = objectMapper.readValue(responseBody, getWorkerResponse());

            if (workerResponse.needRetry()) {
                throw new RetryableException(retryableResponse);
            }

            return workerResponse;
        } catch (RetryableException e) {
            throw e;
        } catch (Exception e) {
            exceptionMsg = e.getMessage();

            throw new RetryableException(e, retryableResponse);
        } finally {
            Map<String, Object> logTransaction = new HashMap<>();
            logTransaction.put("Request", requestBody);
            logTransaction.put("Response", responseBody);
            logTransaction.put("Exception", exceptionMsg);

            String respBody = responseBody.isEmpty() ? exceptionMsg : responseBody;

            double endExec = System.currentTimeMillis();
            double totalTimeExec = (endExec - startExec) / 1000;
            logTransaction.put("Execute time", totalTimeExec + "Sec.");

            log.info("Log Transaction execute(): {}", objectMapper.writeValueAsString(logTransaction));
        }
    }

    @Override
    @Recover
    public Object recover(RetryableException exception, RetryableRequest request) {
        contextRetry.setStrategies(getStrategies());
        return contextRetry.executeStrategy(exception, request);
    }

    protected abstract void validateBody(String responseBody) throws Exception;

    protected abstract Class<? extends WorkerResponse> getWorkerResponse() throws Exception;

    protected abstract List<Class<? extends RetryStrategy>> getStrategies();

}
