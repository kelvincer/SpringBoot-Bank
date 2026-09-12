package com.example.cbbackend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cbbackend.dto.Pay;
import com.example.cbbackend.service.PaymentService;
import com.example.cbbackend.service.Service;
import com.example.cbbackend.service.TokenService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private Service service;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> savePayment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Pay pay) {
        String token = authorization.replace("Bearer ", "");
        ResponseEntity<Map<String, String>> validation = service.validateToken(token);
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        return paymentService.savePayment(pay, tokenService.extractIdentifier(token));
    }

    @GetMapping("/payment")
    public ResponseEntity<?> getPayment(
            @RequestHeader("Authorization") String authorization,
            Pay pay) {
        String token = authorization.replace("Bearer ", "");
        ResponseEntity<Map<String, String>> validation = service.validateToken(token);
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        return paymentService.getPayment(pay);
    }
}
