package com.bank.payment;

import java.math.BigDecimal;

public enum PaymentType {
    TYPE1("type1" , BigDecimal.valueOf(0.05)),
    TYPE2("type2" , BigDecimal.valueOf(0.1)),
    TYPE3("type3" , BigDecimal.valueOf(0.15));

    String value;
    BigDecimal coefficient;

    PaymentType(String value, BigDecimal coefficient) {
        this.value = value;
        this.coefficient = coefficient;
    }
}
