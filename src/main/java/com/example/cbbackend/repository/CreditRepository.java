package com.example.cbbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cbbackend.model.Credit;
import com.example.cbbackend.model.User;

public interface CreditRepository extends JpaRepository<Credit, Long>{

    List<Credit> findByUser(User user);

    Credit findByIdentifier(String identifier);
}