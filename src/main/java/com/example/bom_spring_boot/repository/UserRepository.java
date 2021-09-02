package com.example.bom_spring_boot.repository;

import com.example.bom_spring_boot.entity.Skus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SkusRepository extends JpaRepository<Skus, Long>, JpaSpecificationExecutor {
    List<String> findAllByBuCode(String buCode);
}
