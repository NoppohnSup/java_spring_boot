package com.example.bom_spring_boot.service;

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

    public List<Object> getUserByFullName(String fullName) {
        return userRepository.findAllByFullName(fullName);
    }
}
