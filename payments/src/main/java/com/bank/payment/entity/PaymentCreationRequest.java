package com.bank.payment.entity;

import com.bank.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreationRequest {

    private PaymentType paymentType;

    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    private String currency;
    @NotNull
    private String debtorIban;
    @NotNull
    private String creditorIban;

    private String details;

    private String creditorBankBIC;
}
