package com.example.bom_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BomSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BomSpringBootApplication.class, args);
	}

}
