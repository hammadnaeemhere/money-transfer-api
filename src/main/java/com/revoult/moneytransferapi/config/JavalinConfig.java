package com.revoult.moneytransferapi.config;

import com.revoult.moneytransferapi.controller.AccountController;
import com.revoult.moneytransferapi.utils.PropertyUtils;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavalinConfig {
    private static Javalin javalin;
    private static final Logger log = LoggerFactory.getLogger(JavalinConfig.class);

    private JavalinConfig() {
    }

    /**
     * Starts the REST Server
     */
    public static void start() {
        if (javalin == null) {
            javalin = Javalin.create().start(PropertyUtils.getPropertyAsInteger("server.port"));
            configureRoutes();
        }
    }

    public static void stop() {
        if (javalin != null) {
            log.info("Shutting down application");
            javalin.stop();
        }
    }

    /**
     * Configure the routes and controller mapping
     */
    private static void configureRoutes() {
        log.info("Configuring Routes");
        javalin.get("/accounts", AccountController.findAll);
        javalin.get("/accounts/:accountId", AccountController.findOneByAccountNumber);
        javalin.get("/accounts/:accountId/balance-inquiry", AccountController.balanceInquiry);
        javalin.get("/accounts/:accountId/transaction-history", AccountController.transactionHistory);
        javalin.get("/accounts/:accountId/transfer-history", AccountController.transferHistory);
        javalin.post("/accounts", AccountController.createAccount);
        javalin.post("/accounts/:accountId/deposit", AccountController.depositAccount);
        javalin.post("/accounts/:accountId/withdraw", AccountController.withdrawFromAccount);
        javalin.post("/accounts/:accountId/transfer/:toAccountId", AccountController.transferBetweenAccounts);
    }


}
