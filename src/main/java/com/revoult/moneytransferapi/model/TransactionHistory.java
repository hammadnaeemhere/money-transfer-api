package com.revoult.moneytransferapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revoult.moneytransferapi.constant.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionHistory {
    private Integer transactionHistoryId;
    private Integer accountId;
    private String transactionSummary;
    private BigDecimal transactionAmount;
    private TransactionType type;
    private BigDecimal closingBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private Date transactionDate;

    public Integer getTransactionHistoryId() {
        return transactionHistoryId;
    }

    public void setTransactionHistoryId(Integer transactionHistoryId) {
        this.transactionHistoryId = transactionHistoryId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTransactionSummary() {
        return transactionSummary;
    }

    public void setTransactionSummary(String transactionSummary) {
        this.transactionSummary = transactionSummary;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
