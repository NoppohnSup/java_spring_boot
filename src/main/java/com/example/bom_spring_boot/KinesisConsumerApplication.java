package com.example.bom_spring_boot;

import com.example.bom_spring_boot.factory.RecordProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClientBuilder;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class KinesisConsumerApplication {

    @Value("${custom.kinesis.stream}")
    private String streamName;

    @Value("${custom.kinesis.application}")
    private String applicationName;

    private final RecordProcessorFactory shardRecordProcessorFactory;

    @Autowired
    public KinesisConsumerApplication(RecordProcessorFactory shardRecordProcessorFactory) {
        this.shardRecordProcessorFactory = shardRecordProcessorFactory;
    }

    @PostConstruct
    public void kinesisRecordProcessor() {
        Region region = Region.AP_SOUTHEAST_1;
        DefaultCredentialsProvider provider = DefaultCredentialsProvider.create();

        KinesisAsyncClientBuilder clientBuilder = KinesisAsyncClient.builder().region(region).credentialsProvider(provider);
        KinesisAsyncClient kinesisClient = KinesisClientUtil.createKinesisAsyncClient(clientBuilder);

        /*
         * Sets up configuration for the KCL, including DynamoDB and CloudWatch dependencies. The argument, a
         * shardRecordProcessorFactory, is where the logic for record processing lives.
         */
        DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).credentialsProvider(provider).build();
        CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).credentialsProvider(provider).build();

        ConfigsBuilder configsBuilder = new ConfigsBuilder(
            streamName,
            applicationName,
            kinesisClient,
            dynamoClient,
            cloudWatchClient,
            UUID.randomUUID().toString(),
            shardRecordProcessorFactory
        );

        /*
         * The Scheduler (also called Worker in earlier versions of the KCL) is the entry point to the KCL. This
         * instance is configured with defaults provided by the ConfigsBuilder.
         */
        Scheduler scheduler = new Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig().retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient))
        );

        /*
         * Kickoff the Scheduler. Record processing of the streams.
         */
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }
}
