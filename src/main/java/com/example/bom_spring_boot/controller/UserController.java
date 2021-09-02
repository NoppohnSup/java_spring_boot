package com.example.bom_spring_boot.controller;

import com.example.bom_spring_boot.constant.Response;
import com.example.bom_spring_boot.model.ResponseModel;
import com.example.bom_spring_boot.service.UserService;
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
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<ResponseModel> getUserByFullName(
            @RequestParam(value = "full_name") String fullName
    ) {
        return new ResponseModel(Response.SUCCESS.getContent(), userService.getUserByFullName(fullName)).build(HttpStatus.OK);
    }
}
