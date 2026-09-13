package com.example.cbbackend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CreditResponse {
    private String title;
    private String identifier;
    private String status;
    private Double balance;
    private Double monthlyFee;
    private LocalDate expiration;
    private Double rate;
    private LocalDate initDate;
    private Integer totalTerm;
}