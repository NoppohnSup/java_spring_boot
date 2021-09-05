package com.example.bom_spring_boot.Util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.example.bom_spring_boot.model.Event;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

@Slf4j
public class KinesisUtil {
    private static AWSStaticCredentialsProvider credentialsProvider;

    public KinesisUtil() {
        AWSCredentials credentials;
        try {
            credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        credentialsProvider = new AWSStaticCredentialsProvider(credentials);
    }

    public static PutRecordResult putRecord(Event event, String streamName) throws UnsupportedEncodingException {
        String json = new Gson().toJson(event);

        AmazonKinesis kinesis = AmazonKinesisClientBuilder.standard().withCredentials(credentialsProvider)
                .withRegion(Regions.AP_SOUTHEAST_1).build();
        PutRecordRequest putRecordRequest = new PutRecordRequest().withStreamName(streamName);
        putRecordRequest.setData(ByteBuffer.wrap(json.getBytes("UTF-8")));
        putRecordRequest.setPartitionKey( String.format( "partitionKey-%d", 1 ));

        PutRecordResult putRecordResult = kinesis.putRecord(putRecordRequest);
        log.info("Kinesis putRecordResult : " + putRecordResult);
        return putRecordResult;
    }
}