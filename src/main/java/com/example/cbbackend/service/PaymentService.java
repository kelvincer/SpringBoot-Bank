package com.example.cbbackend.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.cbbackend.dto.Pay;
import com.example.cbbackend.dto.PayResponse;
import com.example.cbbackend.model.Credit;
import com.example.cbbackend.model.Payment;
import com.example.cbbackend.model.User;
import com.example.cbbackend.repository.CreditRepository;
import com.example.cbbackend.repository.PaymentRepository;
import com.example.cbbackend.repository.UserRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> savePayment(Pay pay, String email) {

        Map<String, Object> response = new HashMap<>();

        Credit credit = creditRepository.findByIdentifier(pay.getIdentifier());
        User user = userRepository.findByEmail(email);

        if (user == null || credit == null) {
            response.put("message", "No se encuentra el crédito");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Payment payment = new Payment();
        payment.setTitle(credit.getTitle());
        payment.setIdentifier(credit.getIdentifier());
        payment.setPaidAmount(credit.getMonthlyFee());
        payment.setPayDate(LocalDate.now());
        payment.setOperationNumber("96341");
        payment.setCredit(credit);
        payment.setUser(user);

        paymentRepository.save(payment);

        credit.setBalance(credit.getBalance() - credit.getMonthlyFee());
        credit.setExpiration(credit.getExpiration().plusMonths(1));
        creditRepository.save(credit);

        PayResponse responsePay = new PayResponse();
        responsePay.setTitle(payment.getTitle());
        responsePay.setIdentifier(payment.getIdentifier());
        responsePay.setMonthlyFee(payment.getPaidAmount());
        responsePay.setPaidDate(payment.getPayDate());
        responsePay.setOperation("428343");

        response.put("message", "Pago registrado correctamente");
        response.put("payment", responsePay);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getPayment(Pay pay) {
        Map<String, Object> response = new HashMap<>();

        Payment payment = paymentRepository.findByIdentifier(pay.getIdentifier());

        if (payment == null) {
            response.put("message", "No se encontró el pago");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        PayResponse responsePay = new PayResponse();
        responsePay.setTitle(payment.getTitle());
        responsePay.setIdentifier(payment.getIdentifier());
        responsePay.setMonthlyFee(payment.getPaidAmount());
        responsePay.setPaidDate(payment.getPayDate());
        responsePay.setOperation("428343");

        response.put("payment", responsePay);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getPayment(long id) {
        Map<String, Object> response = new HashMap<>();

        Payment payment = paymentRepository.findById(id).orElse(null);

        if (payment == null) {
            response.put("message", "No se encontró el pago");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        PayResponse responsePay = new PayResponse();
        responsePay.setTitle(payment.getTitle());
        responsePay.setIdentifier(payment.getIdentifier());
        responsePay.setMonthlyFee(payment.getPaidAmount());
        responsePay.setPaidDate(payment.getPayDate());
        responsePay.setOperation("428343");

        response.put("payment", responsePay);
        return ResponseEntity.ok(response);
    }
}
