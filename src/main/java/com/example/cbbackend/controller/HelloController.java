package com.example.cbbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot from Pivotal!";
    }

    @PostMapping("/hello")
    public String hello2() {
        return "Hello Spring Boot from Pivotal!";
    }
}
