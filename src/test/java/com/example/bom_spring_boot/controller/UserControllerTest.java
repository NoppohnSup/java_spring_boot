package com.example.bom_spring_boot.controller;

import com.example.bom_spring_boot.entity.UsersEntity;
import com.example.bom_spring_boot.model.ResponseModel;
import com.example.bom_spring_boot.model.UsersRequest;
import com.example.bom_spring_boot.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {
    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    @Test
    public void test_getUserByFullName_success() {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setId(1L);
        usersEntity.setFullName("full name");
        usersEntity.setNickname("nickname");
        usersEntity.setAge(22);
        when(service.getUserByFullName(anyString())).thenReturn(Arrays.asList(usersEntity));
        HttpEntity<ResponseModel> actual = controller.getUserByFullName("test");

        verify(service).getUserByFullName("test");
        assertEquals("success", actual.getBody().getMessage());
        assertEquals(Arrays.asList(usersEntity), actual.getBody().getData());
    }

    @Test
    public void test_createUser_success() {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setFullName("full name");
        usersEntity.setNickname("nickname");
        usersEntity.setAge(22);

        when(service.createUser(any(UsersRequest.class))).thenReturn(usersEntity);

        UsersRequest usersRequest = new UsersRequest();
        usersEntity.setFullName("full name");
        usersEntity.setNickname("test");
        usersEntity.setAge(22);

        HttpEntity<ResponseModel> actual = controller.createUser(usersRequest);

        verify(service).createUser(usersRequest);
        assertEquals("success", actual.getBody().getMessage());
        assertEquals(usersEntity, actual.getBody().getData());
    }
}