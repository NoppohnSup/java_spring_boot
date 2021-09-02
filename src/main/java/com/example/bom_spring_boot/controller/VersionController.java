package com.example.bom_spring_boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@Slf4j
public class VersionController {
    @GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String index() {
        String versionContent = "version.txt or CATALINA_HOME is not set";
        try {
            versionContent = new String(Files.readAllBytes(Paths.get("version.txt")));
            log.info("Version: {}", versionContent);
        } catch (Exception e) {
            log.error("Get version error: {}", e.getMessage());
        }

        return versionContent;
    }
}
