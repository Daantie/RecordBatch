package com.example.rabobankassignment.exception;

import java.math.BigDecimal;

public class InvalidEndBalanceException extends RuntimeException {
    private final BigDecimal calculatedBalance;

    public InvalidEndBalanceException(String message, BigDecimal calculatedBalance) {
        super(message);
        this.calculatedBalance = calculatedBalance;
    }

    public BigDecimal getCalculatedBalance() {
        return calculatedBalance;
    }
}
