package com.example.cbbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cbbackend.CalculatorLibrary;

@RestController
public class MiController {

    @GetMapping("/sumar")
    public int sumar(@RequestParam int a, @RequestParam int b) {
        return CalculatorLibrary.INSTANCE.sumar(a, b);
    }

    @GetMapping("/saludar")
    public void saludar(@RequestParam String message) {
        CalculatorLibrary.INSTANCE.saludar(message);
    }
}
