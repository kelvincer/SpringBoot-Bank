package com.example.cbbackend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data 
public class PayResponse {
    String title;

    String identifier;

    Double monthlyFee;

    LocalDate paidDate;

    String operation;
}
