package com.revoult.moneytransferapi.constant;

public enum ResponseCode {
    SUCCESS("0000", "Operation Successful"),
    ACCOUNT_CREATED_SUCCESS("0000", "Account created successfully with Account # {}"),
    BALANCE_INQUIRY_SUCCESS("0000", "Account balance retrieved successfully"),
    TRANSACTION_HISTORY_SUCCESS("0000", "Account transaction history retrieved successfully"),
    TRANSFER_HISTORY_SUCCESS("0000", "Account transfer history retrieved successfully"),
    DEPOSIT_SUCCESS("0000", "Amount {} deposited successfully"),
    WITHDRAW_SUCCESS("0000", "Amount {} withdrawn successfully"),
    TRANSFER_SUCCESS("0000", "Amount {} has been successfully transferred to Account # {}"),
    ACCOUNT_NOT_FOUND("0001", "Account with ID: {} doesn't exists"),
    INSUFFICIENT_BALANCE("0002", "Account doesn't have sufficient balance to perform operation"),
    INVALID_AMOUNT("0003", "Invalid value for amount provided"),
    ACCOUNT_CREATION_FAILED("0004", "Failed to create account, please check if all the details are provided"),
    INVALID_TRANSFER_REQUEST("0005", "Transfer can't be processed with same account number");

    private final String code;
    private final String description;

    ResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
