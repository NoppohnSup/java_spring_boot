package com.example.bom_spring_boot.service;

import com.example.bom_spring_boot.entity.UsersEntity;
import com.example.bom_spring_boot.model.UsersRequest;
import com.example.bom_spring_boot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UsersEntity> getUserByFullName(String fullName) {
        return userRepository.findAllByFullName(fullName);
    }

    public UsersEntity createUser(UsersRequest usersRequest) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setFullName(usersRequest.getFullName());
        usersEntity.setNickname(usersRequest.getNickname());
        usersEntity.setAge(usersRequest.getAge());
        userRepository.save(usersEntity);
        return usersEntity;
    }
}
