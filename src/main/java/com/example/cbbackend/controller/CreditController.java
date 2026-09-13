package com.example.cbbackend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cbbackend.service.CreditService;
import com.example.cbbackend.service.Service;

@RestController
@RequestMapping("/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private Service service;

    @GetMapping
    public ResponseEntity<?> getCredits(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        ResponseEntity<Map<String, String>> validation = service.validateToken(token);
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        return creditService.findUserCredits(token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCredit(
            @RequestHeader("Authorization") String authorization,
            @PathVariable long id) {
        String token = authorization.replace("Bearer ", "");
        ResponseEntity<Map<String, String>> validation = service.validateToken(token);
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }
        return creditService.getCredit(id);
    }

}
