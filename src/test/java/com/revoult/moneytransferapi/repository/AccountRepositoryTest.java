package com.revoult.moneytransferapi.repository;

import com.revoult.moneytransferapi.constant.TransactionType;
import com.revoult.moneytransferapi.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class AccountRepositoryTest {

    @Spy
    AccountRepository accountRepository;

    @Test
    public void testDeposit() {
        Account account = new Account();
        account.setAccountId(1234);
        account.setAccountTitle("Title");
        account.setBalance(BigDecimal.TEN);

        BigDecimal amount = BigDecimal.TEN;

        Mockito.when(accountRepository.findOne(1234)).thenReturn(account);
        Mockito.when(accountRepository.insertTransactionHistory(1234, "Some Summary", amount, TransactionType.DEPOSIT.name(), BigDecimal.TEN)).thenReturn(1);

        Account update = accountRepository.deposit(account, amount, "Some Summary");

        Assertions.assertEquals(update.getBalance(), new BigDecimal(10), "Balance is equal returned by findOne");
        Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(1234, new BigDecimal(20));
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).insertTransactionHistory(1234, "Some Summary", amount, TransactionType.DEPOSIT.name(), BigDecimal.TEN);
    }

    @Test
    public void testWithdraw() {
        Account account = new Account();
        account.setAccountId(1234);
        account.setAccountTitle("Title");
        account.setBalance(new BigDecimal(20));

        BigDecimal amount = BigDecimal.TEN;

        Mockito.when(accountRepository.findOne(1234)).thenReturn(account);
        Mockito.when(accountRepository.insertTransactionHistory(1234, "Some Summary", amount, TransactionType.WITHDRAW.name(), new BigDecimal(20))).thenReturn(1);

        Account update = accountRepository.withdraw(account, amount, "Some Summary");

        Assertions.assertEquals(update.getBalance(), new BigDecimal(20), "Balance is same as of account object");

        Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(1234, new BigDecimal(10));
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).insertTransactionHistory(1234, "Some Summary", amount, TransactionType.WITHDRAW.name(), new BigDecimal(20));
    }


    @Test
    public void testTransfer() {
        Account from = new Account();
        from.setAccountId(1234);
        from.setBalance(new BigDecimal(20));

        Account to = new Account();
        to.setAccountId(2345);
        to.setBalance(new BigDecimal(5));


        BigDecimal amount = BigDecimal.TEN;

        Mockito.when(accountRepository.findOne(1234)).thenReturn(from);
        Mockito.when(accountRepository.findOne(2345)).thenReturn(to);

        Mockito.when(accountRepository.insertTransactionHistory(1234, "Amount 10 has been transferred to Account # 2345.", amount, TransactionType.WITHDRAW.name(), new BigDecimal(20))).thenReturn(1);
        Mockito.when(accountRepository.insertTransactionHistory(2345, "Amount 10 has been deposited by Account # 1234.", amount, TransactionType.DEPOSIT.name(), new BigDecimal(5))).thenReturn(2);

        Account update = accountRepository.transfer(from, to, amount);

        Assertions.assertEquals(update.getAccountId(), from.getAccountId(), "From account returned");

        Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(1234, new BigDecimal(10));
        Mockito.verify(accountRepository, Mockito.times(1)).updateBalance(2345, new BigDecimal(15));
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(1234);
        Mockito.verify(accountRepository, Mockito.times(1)).findOne(2345);
        Mockito.verify(accountRepository, Mockito.times(1)).insertTransactionHistory(1234, "Amount 10 has been transferred to Account # 2345.", amount, TransactionType.WITHDRAW.name(), new BigDecimal(20));
        Mockito.verify(accountRepository, Mockito.times(1)).insertTransactionHistory(2345, "Amount 10 has been deposited by Account # 1234.", amount, TransactionType.DEPOSIT.name(), new BigDecimal(5));
        Mockito.verify(accountRepository, Mockito.times(1)).insertTransferHistory(1234, 2345, amount, 1);
    }
}
