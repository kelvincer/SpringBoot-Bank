package com.example.cbbackend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.cbbackend.dto.CreditResponse;
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

    public ResponseEntity<Map<String, Object>> getCredit(Long id) {
        Map<String, Object> response = new HashMap<>();

        try {

            Credit credit = creditRepository.findById(id).orElse(null);

            if (credit == null) {
                response.put("message", "No se encontró el credito");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            CreditResponse creditResponse = new CreditResponse();
            creditResponse.setTitle(credit.getTitle());
            creditResponse.setIdentifier(credit.getIdentifier());
            creditResponse.setStatus(credit.getStatus());
            creditResponse.setBalance(credit.getBalance());
            creditResponse.setMonthlyFee(credit.getMonthlyFee());
            creditResponse.setExpiration(credit.getExpiration());
            creditResponse.setRate(credit.getRate());
            creditResponse.setInitDate(credit.getInitDate());
            creditResponse.setTotalTerm(credit.getTotalTerm());

            response.put("credit", creditResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error obteniendo el credito");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
