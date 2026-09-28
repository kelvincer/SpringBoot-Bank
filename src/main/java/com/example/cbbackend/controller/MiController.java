package com.example.cbbackend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cbbackend.CalculatorLibrary;

@RestController
public class MiController {

    @GetMapping("/sumar")
    public int sumar(@RequestParam int a, @RequestParam int b) {
        return CalculatorLibrary.instance().sumar(a, b);
    }

    @GetMapping("/saludar")
    public void saludar(@RequestParam String message) {
        CalculatorLibrary.instance().saludar(message);
    }

    @ExceptionHandler(UnsatisfiedLinkError.class)
    public ResponseEntity<Map<String, String>> handleMissingNativeLibrary(UnsatisfiedLinkError e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", e.getMessage()));
    }
}
