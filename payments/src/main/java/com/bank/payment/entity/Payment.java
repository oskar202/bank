package com.bank.payment.entity;


import com.bank.payment.PaymentStatus;
import com.bank.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long id;
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private String debtorIban;
    private String creditorIban;
    private String details;
    private String creditorBankBIC;
    private PaymentStatus paymentStatus;
    private BigDecimal cancellationFee;
    private LocalDateTime createdAt;
}
