package com.revoult.moneytransferapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Amount {
    private BigDecimal amount;

    public Amount() {
    }

    public Amount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonProperty("balance")
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return amount != null ? amount.toPlainString() : null;
    }
}
