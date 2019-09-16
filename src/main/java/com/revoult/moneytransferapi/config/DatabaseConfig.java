package com.revoult.moneytransferapi.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.revoult.moneytransferapi.repository.AccountRepository;
import com.revoult.moneytransferapi.repository.TransactionHistoryRepository;
import com.revoult.moneytransferapi.repository.TransferHistoryRepository;
import com.revoult.moneytransferapi.utils.PropertyUtils;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.IOException;

/**
 * This class configures the database layer and repositories.
 */
public class DatabaseConfig extends AbstractModule {

    @SuppressWarnings("UnstableApiUsage")
    @Provides
    @Singleton
    Jdbi dbi() throws IOException {
        Jdbi jdbi = Jdbi.create(
                PropertyUtils.getProperty("jdbc.url"),
                PropertyUtils.getProperty("jdbc.username"),
                PropertyUtils.getProperty("jdbc.password")
        );

        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new H2DatabasePlugin());

        jdbi.useTransaction(h -> {
            h.createScript(Resources.toString(Resources.getResource("schema.sql"), Charsets.UTF_8)).execute();
            h.createScript(Resources.toString(Resources.getResource("data.sql"), Charsets.UTF_8)).execute();
        });

        return jdbi;
    }

    @Provides
    @Singleton
    AccountRepository accountRepository(Jdbi dbi) {
        return dbi.onDemand(AccountRepository.class);
    }

    @Provides
    @Singleton
    TransactionHistoryRepository transactionHistoryRepository(Jdbi dbi) {
        return dbi.onDemand(TransactionHistoryRepository.class);
    }


    @Provides
    @Singleton
    TransferHistoryRepository transferHistoryRepository(Jdbi dbi) {
        return dbi.onDemand(TransferHistoryRepository.class);
    }

}
