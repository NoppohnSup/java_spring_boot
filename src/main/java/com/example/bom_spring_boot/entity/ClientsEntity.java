package com.example.bom_spring_boot.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "clients")
public class ClientsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "config")
    private String config;
}
