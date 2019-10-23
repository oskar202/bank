package com.bank.payment.repository;

import com.bank.payment.entity.Payment;
import com.bank.payment.entity.PaymentCreationRequest;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;

@Repository
public class NotificationRepository {
    private Map<String, Payment> database = new HashMap<>();

    private static long sequence = 1;

    public void saveUnsuccessfulNotify(PaymentCreationRequest request) {
        long id = sequence++;
        Payment payment = Payment.builder()
                .id(id)
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .debtorIban(request.getDebtorIban())
                .creditorIban(request.getCreditorIban())
                .createdAt(now())
                .build();
        database.put(String.valueOf(id), payment);
    }

    public Collection<Payment> findAll() {
        return database.values();
    }

}
