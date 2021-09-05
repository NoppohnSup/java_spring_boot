package com.example.bom_spring_boot.processor;

import com.example.bom_spring_boot.model.Event;
import com.example.bom_spring_boot.service.ThirdPartyService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.exceptions.ThrottlingException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.RecordProcessorCheckpointer;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class RecordProcessor implements ShardRecordProcessor {

    @Autowired
    private ThirdPartyService thirdPartyService;
    @Autowired
    private ObjectMapper objectMapper;

    private String kinesisShardId;
    private static final long BACKOFF_TIME_IN_MILLIS = 3000L;
    private static final int NUM_RETRIES = 1;
    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    @Override
    public void initialize(InitializationInput initializationInput) {
        log.info("Initializing record processor for shardId: {}", initializationInput.shardId());
        this.kinesisShardId = initializationInput.shardId();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        try {
            MDC.put("UUID", "UUID:" + UUID.randomUUID().toString());

            List<KinesisClientRecord> records = processRecordsInput.records();
            log.info("Processing {} records from shardId: {}", records.size(), kinesisShardId);
            processRecordsWithRetries(records);

            checkpoint(processRecordsInput.checkpointer());
        } finally {
            MDC.clear();
        }
    }

    private void processRecordsWithRetries(List<KinesisClientRecord> records) {
        for (KinesisClientRecord record : records) {
            boolean processedSuccessfully = false;
            for (int i = 0; i < NUM_RETRIES; i++) {
                try {
                    processSingleRecord(record);

                    processedSuccessfully = true;
                    break;
                } catch (Throwable t) {
                    log.warn("Caught throwable while processing record {}", record, t);
                }

                // backoff if we encounter an exception.
                try {
                    Thread.sleep(BACKOFF_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    log.debug("Interrupted sleep {}", e.getMessage());
                }
            }

            if (!processedSuccessfully) {
                log.error("Couldn't process record {}. Skipping the record.", record);
            }
        }
    }

    private void processSingleRecord(KinesisClientRecord record) {
        String data = null;
        try {
            data = decoder.decode(record.data()).toString();
            log.info("Received event with Sequence No: {}, data: {}", record.sequenceNumber(), data);

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            Event event = objectMapper.readValue(data, Event.class);

            switch (event.getType()) {
                case "SyncClient":
                    thirdPartyService.handleSyncStockToClient(event);
                    log.info("This event has considered as : Stock movement.");
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getType());
                    break;
            }
        } catch (CharacterCodingException e) {
            log.error("processSingleRecord data: {}, CharacterCodingException: {}", data, e.getMessage(), e);
        } catch (Exception e) {
            log.error("processSingleRecord data: {}, Exception: {}", data, e.getMessage(), e);
        }
    }

    private void checkpoint(RecordProcessorCheckpointer checkpointer) {
        log.info("Checkpointing shard " + kinesisShardId);

        for (int i = 0; i < NUM_RETRIES; i++) {
            try {
                checkpointer.checkpoint();
                break;
            } catch (ShutdownException se) {
                // Ignore checkpoint if the processor instance has been shutdown (fail over).
                log.info("Caught shutdown exception, skipping checkpoint." + se);
                break;
            } catch (ThrottlingException e) {
                // Backoff and re-attempt checkpoint upon transient failures
                if (i >= (NUM_RETRIES - 1)) {
                    log.error("Checkpoint failed after " + (i + 1) + "attempts." + e);
                    break;
                } else {
                    log.info("Transient issue when checkpointing - attempt " + (i + 1) + " of " + NUM_RETRIES + e);
                }
            } catch (InvalidStateException e) {
                // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
                log.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library." + e);
                break;
            }
            try {
                Thread.sleep(BACKOFF_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                log.debug("Interrupted sleep" + e);
            }
        }
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        log.info("RecordProcessor.leaseLost {}", leaseLostInput);
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        try {
            log.info("RecordProcessor.shardEnded {}", shardEndedInput);
            shardEndedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            log.error("RecordProcessor.shardEnded {}", e.getMessage(), e);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        log.info("RecordProcessor.shutdownRequested {}", shutdownRequestedInput);
        checkpoint(shutdownRequestedInput.checkpointer());
    }
}
