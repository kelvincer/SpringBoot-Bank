package com.example.cbbackend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.cbbackend.model.Credit;
import com.example.cbbackend.model.User;
import com.example.cbbackend.repository.CreditRepository;
import com.example.cbbackend.repository.UserRepository;

@Service
public class CreditService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Map<String, Object>> findUserCredits(String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractIdentifier(token);
            User user = userRepository.findByEmail(email);

            if (user == null) {
                response.put("message", "No se encontró al usuario");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<Credit> credits = creditRepository.findByUser(user);

            response.put("credits", credits);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error obteniendo los creditos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
