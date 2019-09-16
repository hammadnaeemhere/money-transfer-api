package com.revoult.moneytransferapi.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revoult.moneytransferapi.service.AccountService;
import com.revoult.moneytransferapi.service.impl.AccountServiceImpl;

/**
 * This configures and provides the DI instance.
 */
public class DependencyInjector extends AbstractModule {

    private static Injector injector;

    static {
        injector = Guice.createInjector(new DependencyInjector());
    }

    private DependencyInjector() {
    }

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        install(new DatabaseConfig());
    }


    public static Injector getInjector() {
        return injector;
    }

    public static <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }
}
