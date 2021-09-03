package com.example.bom_spring_boot.repository;

import com.example.bom_spring_boot.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Long>, JpaSpecificationExecutor {
    List<UsersEntity> findAllByFullName(String name);
}
