package com.example.rabobankassignment.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RecordDTO implements Serializable {
    private final long reference;
    private final String accountNumber;
    private final String description;
    private final BigDecimal startBalance;
    private final BigDecimal mutation;
    private final BigDecimal endBalance;
    private final String reason;

    public RecordDTO(long reference, String accountNumber, String description, BigDecimal startBalance,
                     BigDecimal mutation, BigDecimal endBalance, String reason) {
        this.reference = reference;
        this.accountNumber = accountNumber;
        this.description = description;
        this.startBalance = startBalance;
        this.mutation = mutation;
        this.endBalance = endBalance;
        this.reason = reason;
    }

    public long getReference() {
        return reference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public BigDecimal getMutation() {
        return mutation;
    }

    public BigDecimal getEndBalance() {
        return endBalance;
    }

    public String getReason() {
        return reason;
    }
}
