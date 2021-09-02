package com.example.bom_spring_boot.controller;

import com.example.bom_spring_boot.constant.Response;
import com.example.bom_spring_boot.model.ResponseModel;
import com.example.bom_spring_boot.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping(value = "/skus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<ResponseModel> getSku(
            @RequestParam(value = "bu_code") String buCode
    ) {
        return new ResponseModel(Response.SUCCESS.getContent(), skuService.getSku(buCode)).build(HttpStatus.OK);
    }
}
