package com.revoult.moneytransferapi.service.impl;

import com.revoult.moneytransferapi.constant.ResponseCode;
import com.revoult.moneytransferapi.dto.Amount;
import com.revoult.moneytransferapi.dto.Response;
import com.revoult.moneytransferapi.model.Account;
import com.revoult.moneytransferapi.model.TransactionHistory;
import com.revoult.moneytransferapi.model.TransferHistory;
import com.revoult.moneytransferapi.repository.AccountRepository;
import com.revoult.moneytransferapi.repository.TransactionHistoryRepository;
import com.revoult.moneytransferapi.repository.TransferHistoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    TransferHistoryRepository transferHistoryRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void testCreateAccount() {
        Account account = new Account();
        account.setAccountTitle("Title");
        account.setBalance(BigDecimal.TEN);

        Account savedAccount = new Account();
        savedAccount.setAccountId(1001);
        savedAccount.setAccountTitle("Title");
        savedAccount.setBalance(BigDecimal.TEN);

        Mockito.when(accountRepository.insert(account)).thenReturn(1001);
        Mockito.when(accountRepository.findOne(1001)).thenReturn(savedAccount);

        Response response = accountService.createAccount(account);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.ACCOUNT_CREATED_SUCCESS.getCode(), "status code is of account created success");
        Assertions.assertEquals((response.getData()), savedAccount, "object returned is same as returned by find one");

        Mockito.verify(accountRepository, Mockito.times(1)).insert(account);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1001);
    }

    @Test
    public void testCreateAccountNoTitleProvided() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);

        Response response = accountService.createAccount(account);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.ACCOUNT_CREATION_FAILED.getCode(), "has failed account creation status code");
    }

    @Test
    public void testFindAllAccounts() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);

        Account account1 = new Account();
        account1.setBalance(BigDecimal.ONE);

        Mockito.when(accountRepository.findAll()).thenReturn(Arrays.asList(account, account1));

        Response response = accountService.findAllAccounts();

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.SUCCESS.getCode(), "has success status code");
        Assertions.assertEquals(response.getData(), Arrays.asList(account, account1), "has same list returned by findAll");

        Mockito.verify(accountRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testFindAccountById() {
        Account account = new Account();
        account.setAccountId(1234);
        account.setBalance(BigDecimal.TEN);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(account);

        Response response = accountService.findAccountByAccountId(1234);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.SUCCESS.getCode(), "has success code");
        Assertions.assertEquals(response.getData(), account, "has same account object returned by find one");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
    }

    @Test
    public void testFindAccountByIdInvalidAccountId() {
        Mockito.when(accountRepository.findOne(1234)).thenReturn(null);

        Response response = accountService.findAccountByAccountId(1234);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.ACCOUNT_NOT_FOUND.getCode(), "has account not found status code");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
    }

    @Test
    public void testDepositAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setAccountId(1234);

        Amount amount = new Amount(BigDecimal.TEN);

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(20));
        updatedAccount.setAccountId(1234);


        Mockito.when(accountRepository.findOne(1234)).thenReturn(account);
        Mockito.when(accountRepository.deposit(account, amount.getAmount(), "Amount 10 deposited by self.")).thenReturn(updatedAccount);

        Response response = accountService.depositAccount(1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.DEPOSIT_SUCCESS.getCode(), "has same deposit success code description");
        Assertions.assertEquals(response.getData(), updatedAccount);
        Assertions.assertEquals(response.getDescription(), "Amount 10 deposited successfully", "has same deposit success code description");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).deposit(account, amount.getAmount(), "Amount 10 deposited by self.");
    }

    @Test
    public void testDepositAccountInvalidAmount() {
        Amount amount = new Amount();

        Response response = accountService.depositAccount(1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.INVALID_AMOUNT.getCode(), "has invalid amount code as amount was null");

        Mockito.verify(accountRepository, Mockito.never()).findOne(1234);
        Mockito.verify(accountRepository, Mockito.never()).deposit(Mockito.any(), Mockito.any(), Mockito.anyString());
    }

    @Test
    public void testDepositAccountAccountNotFound() {
        Amount amount = new Amount(BigDecimal.TEN);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(null);

        Response response = accountService.depositAccount(1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.ACCOUNT_NOT_FOUND.getCode(), "has account not found code as account doesn't exists");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.never()).deposit(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testWithdrawFromAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setAccountId(1234);

        Amount amount = new Amount(BigDecimal.TEN);

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(20));
        updatedAccount.setAccountId(1234);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(account);
        Mockito.when(accountRepository.withdraw(account, amount.getAmount(), "Amount 10 withdrawn by self.")).thenReturn(updatedAccount);

        Response response = accountService.withdrawFromAccount(1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.WITHDRAW_SUCCESS.getCode(), "has withdraw success code");
        Assertions.assertEquals(response.getData(), updatedAccount);
        Assertions.assertEquals(response.getDescription(), "Amount 10 withdrawn successfully", "has withdraw success status code");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).withdraw(account, amount.getAmount(), "Amount 10 withdrawn by self.");
    }

    @Test
    public void testWithdrawFromAccountInsufficientBalance() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setAccountId(1234);

        Amount amount = new Amount(new BigDecimal(20));

        Mockito.when(accountRepository.findOne(1234)).thenReturn(account);

        Response response = accountService.withdrawFromAccount(1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.INSUFFICIENT_BALANCE.getCode(), "has insufficient balance status code");
        Assertions.assertEquals(response.getDescription(), "Account doesn't have sufficient balance to perform operation", "has insufficient balance description");
        Assertions.assertNull(response.getData());

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.never()).withdraw(Mockito.any(), Mockito.any(), Mockito.anyString());
    }

    @Test
    public void testWithdrawFromAccountInvalidAmount() {
        Amount amount = new Amount();

        Response response = accountService.withdrawFromAccount(1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.INVALID_AMOUNT.getCode(), "has invalid amount status code");

        Mockito.verify(accountRepository, Mockito.never()).findOne(1234);
        Mockito.verify(accountRepository, Mockito.never()).withdraw(Mockito.any(), Mockito.any(), Mockito.anyString());
    }

    @Test
    public void testTransferBetweenAccounts() {
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.TEN);
        fromAccount.setAccountId(1234);

        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setAccountId(2345);

        Amount amount = new Amount(BigDecimal.TEN);

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(0));
        updatedAccount.setAccountId(1234);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(fromAccount);
        Mockito.when(accountRepository.findOne(2345)).thenReturn(toAccount);

        Mockito.when(accountRepository.transfer(fromAccount, toAccount, amount.getAmount())).thenReturn(updatedAccount);

        Response response = accountService.transferBetweenAccounts(1234, 2345, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.TRANSFER_SUCCESS.getCode(), "has transfer success status code");
        Assertions.assertEquals(response.getData(), updatedAccount);
        Assertions.assertEquals(response.getDescription(), "Amount 10 has been successfully transferred to Account # 2345", "has transfer success description");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(2345);
        Mockito.verify(accountRepository, Mockito.times(1)).transfer(fromAccount, toAccount, amount.getAmount());
    }


    @Test
    public void testTransferBetweenAccountsInsufficientBalance() {
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.TEN);
        fromAccount.setAccountId(1234);

        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setAccountId(2345);

        Amount amount = new Amount(new BigDecimal(20));

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(0));
        updatedAccount.setAccountId(1234);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(fromAccount);
        Mockito.when(accountRepository.findOne(2345)).thenReturn(toAccount);

        Response response = accountService.transferBetweenAccounts(1234, 2345, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.INSUFFICIENT_BALANCE.getCode(), "has insufficient balance status code");
        Assertions.assertNull(null);
        Assertions.assertEquals(response.getDescription(), "Account doesn't have sufficient balance to perform operation");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(2345);

        Mockito.verify(accountRepository, Mockito.never()).transfer(fromAccount, toAccount, amount.getAmount());
    }

    @Test
    public void testTransferBetweenAccountsInvalidFromAccount() {
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.TEN);
        fromAccount.setAccountId(1234);

        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setAccountId(2345);

        Amount amount = new Amount(new BigDecimal(20));

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(0));
        updatedAccount.setAccountId(1234);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(null);
        Mockito.when(accountRepository.findOne(2345)).thenReturn(toAccount);

        Response response = accountService.transferBetweenAccounts(1234, 2345, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.ACCOUNT_NOT_FOUND.getCode(), "has invalid account status code");
        Assertions.assertNull(null);
        Assertions.assertEquals(response.getDescription(), "Account with ID: 1234 doesn't exists", "has invalid account description for from account");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(2345);

        Mockito.verify(accountRepository, Mockito.never()).transfer(fromAccount, toAccount, amount.getAmount());
    }

    @Test
    public void testTransferBetweenAccountsInvalidToAccount() {
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.TEN);
        fromAccount.setAccountId(1234);

        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setAccountId(2345);

        Amount amount = new Amount(new BigDecimal(20));

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(0));
        updatedAccount.setAccountId(1234);

        Mockito.when(accountRepository.findOne(1234)).thenReturn(fromAccount);
        Mockito.when(accountRepository.findOne(2345)).thenReturn(null);

        Response response = accountService.transferBetweenAccounts(1234, 2345, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.ACCOUNT_NOT_FOUND.getCode(), "has account not found status code");
        Assertions.assertNull(null);
        Assertions.assertEquals(response.getDescription(), "Account with ID: 2345 doesn't exists", "has account not found for to account");

        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(2345);

        Mockito.verify(accountRepository, Mockito.never()).transfer(fromAccount, toAccount, amount.getAmount());
    }

    @Test
    public void testTransferBetweenAccountsInvalidAmount() {
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.TEN);
        fromAccount.setAccountId(1234);

        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.ZERO);
        toAccount.setAccountId(2345);

        Amount amount = new Amount(BigDecimal.ZERO);

        Account updatedAccount = new Account();
        updatedAccount.setBalance(new BigDecimal(0));
        updatedAccount.setAccountId(1234);

        Response response = accountService.transferBetweenAccounts(1234, 2345, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.INVALID_AMOUNT.getCode());
        Assertions.assertNull(null);
        Assertions.assertEquals(response.getDescription(), "Invalid value for amount provided");

        Mockito.verify(accountRepository, Mockito.never()).findOne(1234);
        Mockito.verify(accountRepository, Mockito.never()).findOne(2345);

        Mockito.verify(accountRepository, Mockito.never()).transfer(fromAccount, toAccount, amount.getAmount());
    }

    @Test
    public void testTransferBetweenAccountsSameAccountAmount() {

        Amount amount = new Amount(BigDecimal.ZERO);

        Response response = accountService.transferBetweenAccounts(1234, 1234, amount);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.INVALID_TRANSFER_REQUEST.getCode(), "expecting invalid transfer response code");
        Assertions.assertNull(null);
        Assertions.assertEquals(response.getDescription(), "Transfer can't be processed with same account number");

        Mockito.verify(accountRepository, Mockito.never()).findOne(1234);

        Mockito.verify(accountRepository, Mockito.never()).transfer(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testBalanceInquiry() {

        Mockito.when(accountRepository.findAccountBalance(1234)).thenReturn(BigDecimal.TEN);

        Response response = accountService.balanceInquiry(1234);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.BALANCE_INQUIRY_SUCCESS.getCode());
        Assertions.assertEquals(((Amount) response.getData()).getAmount(), BigDecimal.TEN);

        Mockito.verify(accountRepository, Mockito.times(1)).findAccountBalance(1234);
    }

    @Test
    public void testTransactionHistory() {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionHistoryId(1);

        TransactionHistory transactionHistory1 = new TransactionHistory();
        transactionHistory1.setTransactionHistoryId(2);

        Mockito.when(transactionHistoryRepository.findAll(1234)).thenReturn(Arrays.asList(transactionHistory, transactionHistory1));

        Response response = accountService.transactionHistory(1234);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.TRANSACTION_HISTORY_SUCCESS.getCode());
        Assertions.assertEquals(response.getData(), Arrays.asList(transactionHistory, transactionHistory1));

        Mockito.verify(transactionHistoryRepository, Mockito.times(1)).findAll(1234);
    }

    @Test
    public void testTransferHistory() {
        TransferHistory transferHistory = new TransferHistory();
        transferHistory.setTransferHistoryId(1);

        TransferHistory transferHistory1 = new TransferHistory();
        transferHistory1.setTransferHistoryId(2);

        Mockito.when(transferHistoryRepository.findAll(1234)).thenReturn(Arrays.asList(transferHistory, transferHistory1));

        Response response = accountService.transferHistory(1234);

        Assertions.assertEquals(response.getStatusCode(), ResponseCode.TRANSFER_HISTORY_SUCCESS.getCode());
        Assertions.assertEquals(response.getData(), Arrays.asList(transferHistory, transferHistory1));

        Mockito.verify(transferHistoryRepository, Mockito.times(1)).findAll(1234);
    }
}
