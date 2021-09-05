package com.example.bom_spring_boot.factory;

import com.example.bom_spring_boot.processor.RecordProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
public class RecordProcessorFactory implements ShardRecordProcessorFactory {

    private BeanFactory beanFactory;

    public RecordProcessorFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return beanFactory.getBean(RecordProcessor.class);
    }
}
