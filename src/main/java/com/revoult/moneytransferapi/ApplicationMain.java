package com.revoult.moneytransferapi;

import com.revoult.moneytransferapi.config.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationMain {
    private static final Logger log = LoggerFactory.getLogger(ApplicationMain.class);

    public static void main(String[] args) {
        startServer();
        configureShutdownHook();
    }

    public static void startServer() {
        log.info("Starting Application");
        JavalinConfig.start();
    }

    static void configureShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(JavalinConfig::stop));
    }


}
