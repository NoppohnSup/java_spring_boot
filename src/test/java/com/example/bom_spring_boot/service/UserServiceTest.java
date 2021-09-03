package com.example.bom_spring_boot.service;

import com.example.bom_spring_boot.entity.UsersEntity;
import com.example.bom_spring_boot.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Test
    public void test_getUserByFullName_success() {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setFullName("test test");
        usersEntity.setNickname("test");
        usersEntity.setAge(25);

        when(repository.findAllByFullName(anyString())).thenReturn(Arrays.asList(usersEntity));
        List<UsersEntity> actual = service.getUserByFullName("test test");

        verify(repository).findAllByFullName("test test");
        assertEquals(Arrays.asList(usersEntity), actual);
    }
}