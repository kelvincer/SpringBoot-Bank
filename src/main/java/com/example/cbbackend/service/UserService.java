package com.example.cbbackend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.cbbackend.dto.Login;
import com.example.cbbackend.model.User;
import com.example.cbbackend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Map<String, String>> validateUser(Login login) {
        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword());

        if (user == null) {
            response.put("message", "Correo o contraseña incorrecto");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("token", "token123");
        response.put("message", "Autenticación correcta");
        return ResponseEntity.ok(response);
    }

}
