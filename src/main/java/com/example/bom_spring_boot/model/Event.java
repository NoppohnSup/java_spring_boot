package com.example.bom_spring_boot.model;

import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.example.bom_spring_boot.Util.KinesisUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class Event {
    private String type;
    private String created_at;
    private String created_by;
    private String producer;
    private String action;
    private String client_name;
    private Object data;

    public Event() { }

    public Event(String type, String createdAt, String createdBy, String producer, String action, List<Map<String, Object>> data) {
        this.type = type;
        this.created_at = createdAt;
        this.created_by = createdBy;
        this.producer = producer;
        this.action = action;
        this.data = data;
    }

    public PutRecordResult push(String streamName) throws UnsupportedEncodingException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'GMT'XXX");
        Date date = new Date();
        created_at = dateFormat.format(date);
        KinesisUtil kinesisUtil = new KinesisUtil();
        return kinesisUtil.putRecord(this, streamName);
    }
}
