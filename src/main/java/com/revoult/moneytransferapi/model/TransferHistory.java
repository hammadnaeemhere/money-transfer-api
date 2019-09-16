package com.revoult.moneytransferapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class TransferHistory {
    private Integer transferHistoryId;
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;
    private Integer transactionHistoryId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private Date transferDate;

    public Integer getTransferHistoryId() {
        return transferHistoryId;
    }

    public void setTransferHistoryId(Integer transferHistoryId) {
        this.transferHistoryId = transferHistoryId;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public Integer getTransactionHistoryId() {
        return transactionHistoryId;
    }

    public void setTransactionHistoryId(Integer transactionHistoryId) {
        this.transactionHistoryId = transactionHistoryId;
    }
}
