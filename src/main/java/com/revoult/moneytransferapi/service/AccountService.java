package com.revoult.moneytransferapi.service;

import com.revoult.moneytransferapi.dto.Amount;
import com.revoult.moneytransferapi.dto.Response;
import com.revoult.moneytransferapi.model.Account;

public interface AccountService {

    Response findAllAccounts();

    Response findAccountByAccountId(Integer accountId);

    Response depositAccount(Integer accountId, Amount amount);

    Response withdrawFromAccount(Integer accountId, Amount amount);

    Response transferBetweenAccounts(Integer fromAccountId, Integer toAccountId, Amount amount);

    Response transactionHistory(Integer accountId);

    Response transferHistory(Integer accountId);

    Response balanceInquiry(Integer accountId);

    Response createAccount(Account account);
}
