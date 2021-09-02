package com.example.bom_spring_boot.service;

import com.example.bom_spring_boot.repository.SkusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SkuService {
    @Autowired
    private SkusRepository skusRepository;

    public List<String> getSku(String buCode) {
        return skusRepository.findAllByBuCode(buCode);
    }
}
