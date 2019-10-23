package com.bank;

import com.bank.payment.Payment;
import com.bank.payment.PaymentRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Endpoint(id = "database")
public class ActuatorDatabase {

    private PaymentRepository paymentRepository;

    public ActuatorDatabase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @ReadOperation(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findAllUsers() {
        Gson parser = new GsonBuilder().setPrettyPrinting().create();
        String json = parser.toJson(paymentRepository.findAll().stream().collect(Collectors.toMap(Payment::getId, payment -> payment)));
        return ResponseEntity.ok().body(json);
    }
}
