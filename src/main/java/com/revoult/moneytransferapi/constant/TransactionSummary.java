package com.revoult.moneytransferapi.constant;

public enum TransactionSummary {
    WITHDRAW("Amount {} withdrawn by self."),
    DEPOSIT("Amount {} deposited by self."),
    WITHDRAW_TRANSFER("Amount {} has been transferred to Account # {}."),
    DEPOSIT_TRANSFER("Amount {} has been deposited by Account # {}.");

    private final String description;

    TransactionSummary(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
}
