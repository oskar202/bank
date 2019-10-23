package com.bank;

import lombok.Data;

@Data
public class OkError {
    private String errorCode;
    private String errorMessage;

    public OkError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
