package com.example.bom_spring_boot.controller;

import com.example.bom_spring_boot.constant.Response;
import com.example.bom_spring_boot.model.ResponseModel;
import com.example.bom_spring_boot.model.UsersRequest;
import com.example.bom_spring_boot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping(value = "users/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<ResponseModel> createUser(
            @Valid @RequestBody UsersRequest usersRequest
    )
    {
        return new ResponseModel(Response.SUCCESS.getContent(), userService.createUser(usersRequest)).build(HttpStatus.OK);
    }
}
