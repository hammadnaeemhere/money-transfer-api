package com.revoult.moneytransferapi.repository;

import com.revoult.moneytransferapi.model.TransactionHistory;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface TransactionHistoryRepository {
    @SqlQuery("select * from transaction_history where account_id = ? order by transaction_date desc")
    @RegisterBeanMapper(TransactionHistory.class)
    List<TransactionHistory> findAll(Integer accountId);
}
