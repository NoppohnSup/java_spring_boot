package com.example.bom_spring_boot.controller;

import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.example.bom_spring_boot.constant.Response;
import com.example.bom_spring_boot.model.Event;
import com.example.bom_spring_boot.model.ResponseModel;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class EventController {

    @Value("${custom.kinesis.stream}")
    String streamName;

    @RequestMapping(value = "/event/create", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> createEvent(@RequestBody Event event) {
        try {
            log.info("EventController.createEvent() request : {}", event);

            Event eventObj = new Event();
            eventObj.setClient_name(event.getClient_name());
            eventObj.setType(event.getType());
            eventObj.setProducer(event.getProducer());
            eventObj.setAction(event.getAction());
            eventObj.setCreated_at(event.getCreated_at());
            eventObj.setCreated_by(event.getCreated_by());
            eventObj.setData(event.getData());
            PutRecordResult putRecordResult = eventObj.push(streamName);

            return new ResponseModel(Response.SUCCESS.getContent(), putRecordResult).build(HttpStatus.OK);
        } catch (Exception e) {
            String exception = Throwables.getStackTraceAsString(e);
            log.error("EventController.createEvent() Exception : {}", exception);

            return new ResponseModel(Response.SUCCESS.getContent(), e.getMessage()).build(HttpStatus.CONFLICT);
        }
    }

}
