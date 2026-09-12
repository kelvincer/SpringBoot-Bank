package com.example.cbbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cbbackend.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByIdentifier(String identifier);
}
