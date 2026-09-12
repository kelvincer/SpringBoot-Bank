package com.example.cbbackend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<Map<String, String>> validateToken(String token) {
        Map<String, String> response = new HashMap<>();
        if (tokenService.validateToken(token)) {
            response.put("message", "Token no es válido");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Token inválido o expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
