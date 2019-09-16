package com.revoult.moneytransferapi.repository;

import com.revoult.moneytransferapi.constant.TransactionSummary;
import com.revoult.moneytransferapi.constant.TransactionType;
import com.revoult.moneytransferapi.model.Account;
import com.revoult.moneytransferapi.utils.StringUtils;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {

    @SqlUpdate("insert into account (ACCOUNT_TITLE, BALANCE) values (:account.accountTitle, :account.balance)")
    @GetGeneratedKeys("account_id")
    Integer insert(@BindBean("account") Account account);

    @SqlQuery("select * from account where account_id = ?")
    @RegisterBeanMapper(Account.class)
    Account findOne(Integer accountId);

    @SqlQuery("select balance from account where account_id = ?")
    BigDecimal findAccountBalance(Integer accountId);

    @SqlQuery("select * from account")
    @RegisterBeanMapper(Account.class)
    List<Account> findAll();

    @SqlUpdate("update account set balance=:balance, last_updated=CURRENT_TIMESTAMP where account_id = :accountId")
    @Transaction
    void updateBalance(@Bind("accountId") Integer accountId, @Bind("balance") BigDecimal balance);

    @SqlUpdate("insert into transaction_history (account_id, transaction_summary, transaction_amount, type, closing_balance) " +
            "values (:accountId, :transactionSummary, :amount, :type, :closingBalance)")
    @GetGeneratedKeys("transaction_history_id")
    @Transaction
    Integer insertTransactionHistory(@Bind("accountId") Integer accountId,
                                     @Bind("transactionSummary") String transactionSummary,
                                     @Bind("amount") BigDecimal amount,
                                     @Bind("type") String type,
                                     @Bind("closingBalance") BigDecimal closingBalance);

    @SqlUpdate("insert into transfer_history (from_account_id, to_account_id, amount, transaction_history_id) " +
            "values (:fromAccountId, :toAccountId, :amount, :transactionHistoryId)")
    @Transaction
    void insertTransferHistory(@Bind("fromAccountId") Integer fromAccountId,
                               @Bind("toAccountId") Integer toAccountId,
                               @Bind("amount") BigDecimal amount,
                               @Bind("transactionHistoryId") Integer transactionHistoryId);


    /**
     * This function deposits the amount in an account.
     *
     * @param account Account for which amount is to be deposit.
     * @param amount  Amount to be deposit.
     * @param summary Transaction Summary.
     * @return Account with updated balance
     */
    @Transaction
    default Account deposit(Account account, BigDecimal amount, String summary) {
        updateBalance(account.getAccountId(), account.getBalance().add(amount));
        Account updated = findOne(account.getAccountId());

        insertTransactionHistory(
                account.getAccountId(),
                summary,
                amount,
                TransactionType.DEPOSIT.name(),
                updated.getBalance()
        );

        return updated;
    }

    /**
     * This function withdraws the amount from an account.
     *
     * @param account Account from which amount is to be withdrawn.
     * @param amount  Amount to be withdrawn.
     * @param summary Transaction Summary.
     * @return Account with updated balance
     */
    @Transaction
    default Account withdraw(Account account, BigDecimal amount, String summary) {
        updateBalance(account.getAccountId(), account.getBalance().subtract(amount));
        Account updated = findOne(account.getAccountId());

        insertTransactionHistory(
                account.getAccountId(),
                summary,
                amount,
                TransactionType.WITHDRAW.name(),
                updated.getBalance()
        );
        return updated;
    }

    /**
     * This function transfers the amount from one account to another making deposit and withdrawal
     * for respective accounts.
     *
     * @param from   From Account
     * @param to     To Account
     * @param amount Amount to be transferred
     * @return From Account with updated balance.
     */
    @Transaction
    default Account transfer(Account from, Account to, BigDecimal amount) {
        updateBalance(from.getAccountId(), from.getBalance().subtract(amount));
        Account updated = findOne(from.getAccountId());

        Integer transactionId = insertTransactionHistory(
                from.getAccountId(),
                StringUtils.formatString(TransactionSummary.WITHDRAW_TRANSFER.getDescription(), amount, to.getAccountId()),
                amount,
                TransactionType.WITHDRAW.name(),
                updated.getBalance()
        );
        deposit(to, amount, StringUtils.formatString(TransactionSummary.DEPOSIT_TRANSFER.getDescription(), amount, from.getAccountId()));

        insertTransferHistory(
                from.getAccountId(),
                to.getAccountId(),
                amount,
                transactionId
        );

        return updated;
    }

}
