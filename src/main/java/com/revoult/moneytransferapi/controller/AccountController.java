package com.revoult.moneytransferapi.controller;

import com.revoult.moneytransferapi.config.DependencyInjector;
import com.revoult.moneytransferapi.dto.Amount;
import com.revoult.moneytransferapi.model.Account;
import com.revoult.moneytransferapi.service.AccountService;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private static AccountService accountService;

    static {
        accountService = DependencyInjector.getInstance(AccountService.class);
    }

    public static Handler findAll = ctx -> {
        log.info("find all accounts requested");
        ctx.json(accountService.findAllAccounts());
    };

    public static Handler findOneByAccountNumber = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        log.info("find account by id {}", accountId);
        ctx.json(accountService.findAccountByAccountId(accountId));
    };

    public static Handler depositAccount = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        log.info("Deposit Request for Account With ID {}", accountId);
        log.debug("Deposit Request from Account Payload : {}", ctx.body());
        Amount amount = ctx.bodyAsClass(Amount.class);

        ctx.json(accountService.depositAccount(accountId, amount));
    };

    public static Handler withdrawFromAccount = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        log.info("Withdraw Request from Account With ID {}", accountId);
        log.debug("Withdraw Request from Account Payload : {}", ctx.body());
        Amount amount = ctx.bodyAsClass(Amount.class);
        ctx.json(accountService.withdrawFromAccount(accountId, amount));
    };

    public static Handler balanceInquiry = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        log.info("Balance Inquiry Request for Account With ID {}", accountId);
        ctx.json(accountService.balanceInquiry(accountId));
    };

    public static Handler transactionHistory = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        log.info("Transaction History Request for Account With ID {}", accountId);
        ctx.json(accountService.transactionHistory(accountId));
    };
    public static Handler transferHistory = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        log.info("Transfer History Request for Account With ID {}", accountId);
        ctx.json(accountService.transferHistory(accountId));
    };

    public static Handler transferBetweenAccounts = ctx -> {
        Integer accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("accountId")));
        Integer toAccountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("toAccountId")));
        log.info("Transfer Request from Account With ID {} to Account ID {}", accountId, toAccountId);
        log.debug("Transfer Request from Account Payload : {}", ctx.body());
        Amount amount = ctx.bodyAsClass(Amount.class);

        ctx.json(accountService.transferBetweenAccounts(accountId, toAccountId, amount));
    };

    public static Handler createAccount = ctx -> {
        Account account = ctx.bodyAsClass(Account.class);
        log.info("Create Account Request {}", ctx.body());
        ctx.json(accountService.createAccount(account));
    };
}
