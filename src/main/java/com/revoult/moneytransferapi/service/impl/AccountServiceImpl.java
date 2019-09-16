package com.revoult.moneytransferapi.service.impl;

import com.google.inject.Inject;
import com.revoult.moneytransferapi.constant.ResponseCode;
import com.revoult.moneytransferapi.constant.TransactionSummary;
import com.revoult.moneytransferapi.dto.Amount;
import com.revoult.moneytransferapi.dto.Response;
import com.revoult.moneytransferapi.model.Account;
import com.revoult.moneytransferapi.repository.AccountRepository;
import com.revoult.moneytransferapi.repository.TransactionHistoryRepository;
import com.revoult.moneytransferapi.repository.TransferHistoryRepository;
import com.revoult.moneytransferapi.service.AccountService;
import com.revoult.moneytransferapi.utils.ResponseUtils;
import com.revoult.moneytransferapi.utils.StringUtils;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private Map<Integer, Lock> accountLocks = new HashMap<>();

    @Inject
    AccountRepository accountRepository;

    @Inject
    TransactionHistoryRepository transactionHistoryRepository;

    @Inject
    TransferHistoryRepository transferHistoryRepository;

    /**
     * This function creates a new account with the provided details
     *
     * @param account Account details
     * @return Account with ID.
     */
    @Override
    public Response createAccount(Account account) {
        //check if account title is provided
        if (account.getAccountTitle() != null) {
            account.setBalance(account.getBalance() == null ? new BigDecimal(0) : account.getBalance());
            Integer accountId = accountRepository.insert(account);
            return ResponseUtils.createResponseDataWithArgs(ResponseCode.ACCOUNT_CREATED_SUCCESS, accountRepository.findOne(accountId), accountId);
        }
        log.info("Account creation failed");
        return ResponseUtils.createResponse(ResponseCode.ACCOUNT_CREATION_FAILED);
    }

    /**
     * Returns all the accounts. This is for testing purposes.
     *
     * @return Account List
     */
    @Override
    public Response findAllAccounts() {
        return ResponseUtils.createResponse(ResponseCode.SUCCESS, accountRepository.findAll());
    }

    /**
     * Returns the single account.
     *
     * @param accountId Account ID
     * @return Account
     */
    @Override
    public Response findAccountByAccountId(Integer accountId) {
        Account account = accountRepository.findOne(accountId);
        if (account != null) {
            return ResponseUtils.createResponse(ResponseCode.SUCCESS, account);
        } else {
            log.info("Account not found with ID {}", accountId);
            return ResponseUtils.createResponseWithArgs(ResponseCode.ACCOUNT_NOT_FOUND, accountId);
        }
    }

    /**
     * Deposits the provided amount in the account.
     *
     * @param accountId Account ID
     * @param amount    Amount to be deposited
     * @return Updated account
     */
    @Override
    @Transaction
    public Response depositAccount(Integer accountId, Amount amount) {
        try {
            lockAccount(accountId);

            if (isValidAmount(amount)) {
                Account account = accountRepository.findOne(accountId);
                //check if account exists
                if (account != null) {
                    return ResponseUtils.createResponseDataWithArgs(
                            ResponseCode.DEPOSIT_SUCCESS,
                            accountRepository.deposit(account, amount.getAmount(), StringUtils.formatString(TransactionSummary.DEPOSIT.getDescription(), amount.getAmount())),
                            amount.getAmount()
                    );
                } else {
                    log.info("Account not found with ID {}", accountId);
                    return ResponseUtils.createResponseWithArgs(ResponseCode.ACCOUNT_NOT_FOUND, accountId);
                }
            } else {
                log.info("Invalid value for amount provided");
                return ResponseUtils.createResponseWithArgs(ResponseCode.INVALID_AMOUNT);
            }
        } finally {
            unLockAccount(accountId);
        }
    }


    /**
     * This function withdraws the amount from an account.
     *
     * @param accountId Account Id
     * @param amount    Amount to be withdrawn
     * @return Account with updated balance
     */
    @Override
    public Response withdrawFromAccount(Integer accountId, Amount amount) {
        try {
            //lock the account
            lockAccount(accountId);
            if (isValidAmount(amount)) {
                Account account = accountRepository.findOne(accountId);
                // check if account exists
                if (account != null) {
                    //check if has sufficient balance
                    if (account.getBalance().compareTo(amount.getAmount()) >= 0) {
                        return ResponseUtils.createResponseDataWithArgs(
                                ResponseCode.WITHDRAW_SUCCESS,
                                accountRepository.withdraw(
                                        account,
                                        amount.getAmount(),
                                        StringUtils.formatString(TransactionSummary.WITHDRAW.getDescription(), amount.getAmount())),
                                amount.getAmount()
                        );
                    } else {
                        log.info("Account {} doesn't have sufficient balance", accountId);
                        return ResponseUtils.createResponse(ResponseCode.INSUFFICIENT_BALANCE);
                    }
                } else {
                    log.info("Account not found with ID {}", accountId);
                    return ResponseUtils.createResponseWithArgs(ResponseCode.ACCOUNT_NOT_FOUND, accountId);
                }
            } else {
                log.info("Invalid value for amount provided");
                return ResponseUtils.createResponse(ResponseCode.INVALID_AMOUNT);
            }
        } finally {
            unLockAccount(accountId);
        }
    }

    /**
     * This transfers the amount from one account to another.
     *
     * @param fromAccountId Account from which amount is to be withdrawn
     * @param toAccountId   Account in which amount is to be deposited
     * @param amount        Amount
     * @return From Account with updated balance.
     */
    @Override
    public Response transferBetweenAccounts(Integer fromAccountId, Integer toAccountId, Amount amount) {
        if (!fromAccountId.equals(toAccountId)) {
            try {
                lockAccount(fromAccountId);
                lockAccount(toAccountId);

                if (isValidAmount(amount)) {
                    Account fromAccount = accountRepository.findOne(fromAccountId);
                    Account toAccount = accountRepository.findOne(toAccountId);
                    //check if from account exists
                    if (fromAccount != null) {
                        //check of to account exists
                        if (toAccount != null) {
                            //check if account has sufficient balance
                            if (fromAccount.getBalance().compareTo(amount.getAmount()) >= 0) {
                                return ResponseUtils.createResponseDataWithArgs(
                                        ResponseCode.TRANSFER_SUCCESS,
                                        accountRepository.transfer(fromAccount, toAccount, amount.getAmount()),
                                        amount.getAmount(), toAccount.getAccountId()
                                );
                            } else {
                                log.info("Account {} doesn't have sufficient balance", fromAccountId);
                                return ResponseUtils.createResponse(ResponseCode.INSUFFICIENT_BALANCE);
                            }
                        } else {
                            log.info("Account not found with ID {} ", toAccountId);
                            return ResponseUtils.createResponseWithArgs(ResponseCode.ACCOUNT_NOT_FOUND, toAccountId);
                        }
                    } else {
                        log.info("Account not found with ID {} ", fromAccount);
                        return ResponseUtils.createResponseWithArgs(ResponseCode.ACCOUNT_NOT_FOUND, fromAccountId);
                    }
                } else {
                    return ResponseUtils.createResponse(ResponseCode.INVALID_AMOUNT);
                }

            } finally {
                unLockAccount(fromAccountId);
                unLockAccount(toAccountId);
            }
        } else {
            return ResponseUtils.createResponse(ResponseCode.INVALID_TRANSFER_REQUEST);
        }
    }

    /**
     * Returns the balance for an account.
     *
     * @param accountId Account Id
     * @return Account balance
     */
    @Override
    public Response balanceInquiry(Integer accountId) {
        return ResponseUtils.createResponse(ResponseCode.BALANCE_INQUIRY_SUCCESS, new Amount(accountRepository.findAccountBalance(accountId)));
    }

    /**
     * Returns the transaction history for an account.
     *
     * @param accountId Account Id
     * @return Account Transaction History List.
     */
    @Override
    public Response transactionHistory(Integer accountId) {
        return ResponseUtils.createResponse(ResponseCode.TRANSACTION_HISTORY_SUCCESS, transactionHistoryRepository.findAll(accountId));
    }

    /**
     * Returns the transfer history for an account/
     *
     * @param accountId Account Id
     * @return Account transfer history list.
     */
    @Override
    public Response transferHistory(Integer accountId) {
        return ResponseUtils.createResponse(ResponseCode.TRANSFER_HISTORY_SUCCESS, transferHistoryRepository.findAll(accountId));
    }


    /**
     * This checks whether the amount provided is not null and greater than 0.
     *
     * @param amount Amount to be validated
     * @return boolean
     */
    private boolean isValidAmount(Amount amount) {
        log.debug("Validating  Amount {}", amount);
        return (amount != null && amount.getAmount() != null && amount.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }


    /**
     * This accounts the lock while performing any operation over that account to make sure concurrency.
     * https://stackoverflow.com/questions/17069569/thread-synchronization-based-upon-an-id
     *
     * @param accountId Account Id to be locked
     */
    private void lockAccount(Integer accountId) {
        log.debug("Locking Account {}", accountId);
        accountLocks.putIfAbsent(accountId, new ReentrantLock());
        accountLocks.get(accountId).lock();
    }

    /**
     * This unlocks the account so that other operations can be performed on that account.
     *
     * @param accountId Account to be unlocked
     */
    private void unLockAccount(Integer accountId) {
        log.debug("Unlocking Account {}", accountId);
        accountLocks.get(accountId).unlock();
    }
}
