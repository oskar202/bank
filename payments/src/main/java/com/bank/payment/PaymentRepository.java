package com.bank.payment;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.bank.payment.PaymentStatus.CANCELLED;
import static com.bank.payment.PaymentStatus.CREATED;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Repository
public class PaymentRepository {
    private Map<String, Payment> database = new HashMap<>();

    private static long sequence = 1;

    void createNewPayment(PaymentCreationRequest request) {
        long id = sequence++;
        Payment payment = Payment.builder()
                .id(id)
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .debtorIban(request.getDebtorIban())
                .creditorIban(request.getCreditorIban())
                .details(request.getDetails())
                .creditorBankBIC(request.getCreditorBankBIC())
                .paymentStatus(CREATED)
                .createdAt(now())
                .build();
        database.put(String.valueOf(id), payment);
    }

    public BigDecimal findCancellationFeeById(Long paymentId) {
        return database.get(String.valueOf(paymentId)).getCancellationFee();
    }

    void cancelPayment(Payment payment) {
        database.put(String.valueOf(payment.getId()), payment);
    }

    public Collection<Long> findAllNotCancelledSortedByAmount() {
        return database.values().stream()
                .sorted(Comparator.comparing(Payment::getAmount))
                .filter(p -> p.getPaymentStatus() != CANCELLED)
                .map(Payment::getId)
                .collect(toList());
    }

    public Payment findOneNotCancelled(Long paymentId) {
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return database.values().stream()
                .filter(p -> p.getId().equals(paymentId) && p.getPaymentStatus() != CANCELLED)
                .filter(p -> p.getCreatedAt().isBefore(endOfToday) && p.getCreatedAt().isAfter(startOfToday))
                .findFirst().orElse(null);
    }

    public Collection<Payment> findAll() {
        return database.values();
    }

}
