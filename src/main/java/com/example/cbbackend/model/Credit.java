package com.example.cbbackend.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "credits")
@Data 
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "el título no puede ser nulo")
    private String title;

    @NotNull(message = "el identificador no puede ser nulo")
    private String identifier;

    @NotNull(message = "el estado no puede ser nulo")
    private String status;

    @NotNull(message = "el saldo no puede ser nulo")
    private Double balance;

    @NotNull(message = "la cuota mensual no puede ser nula")
    private Double monthlyFee;

    @NotNull(message = "la fecha de vencimiento no puede ser nula")
    private LocalDate expiration;

    @NotNull(message = "la tasa no puede ser nula")
    private Double rate;

    @NotNull(message = "la fecha de inicio no puede ser nula")
    private LocalDate initDate;

    @NotNull(message = "el plazo total no puede ser nulo")
    private Integer totalTerm;

    @NotNull(message = "el usuario no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}