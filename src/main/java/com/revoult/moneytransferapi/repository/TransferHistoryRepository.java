package com.revoult.moneytransferapi.repository;

import com.revoult.moneytransferapi.model.TransferHistory;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface TransferHistoryRepository {
    @SqlQuery("select * from transfer_history where from_account_id = ? order by transfer_date desc")
    @RegisterBeanMapper(TransferHistory.class)
    List<TransferHistory> findAll(Integer accountId);
}
