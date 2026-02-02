package com.blog.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Set default timezone to UTC+1 (Central European Time)
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
    }
}
