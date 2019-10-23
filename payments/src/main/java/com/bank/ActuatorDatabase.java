package com.bank;

import com.bank.payment.entity.Payment;
import com.bank.payment.repository.NotificationRepository;
import com.bank.payment.repository.PaymentRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Component
@RestControllerEndpoint(id = "database")
public class ActuatorDatabase {

    private PaymentRepository paymentRepository;
    private NotificationRepository notificationRepository;

    public ActuatorDatabase(PaymentRepository paymentRepository, NotificationRepository notificationRepository) {
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping(value = "payments" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findAllUsers() {
        Gson parser = new GsonBuilder().setPrettyPrinting().create();
        String json = parser.toJson(paymentRepository.findAll().stream().collect(Collectors.toMap(Payment::getId, payment -> payment)));
        return ResponseEntity.ok().body(json);
    }

    @GetMapping(value = "failed-notifications", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findAllFailedNotifications() {
        Gson parser = new GsonBuilder().setPrettyPrinting().create();
        String json = parser.toJson(notificationRepository.findAll().stream().collect(Collectors.toMap(Payment::getId, payment -> payment)));
        return ResponseEntity.ok().body(json);
    }
}
