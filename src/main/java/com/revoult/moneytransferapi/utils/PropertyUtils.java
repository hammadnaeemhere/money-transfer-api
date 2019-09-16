package com.revoult.moneytransferapi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class loads the properties from application.properties file.
 */
public final class PropertyUtils {
    private static Properties properties;
    private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);

    static {
        properties = new Properties();
        log.info("loading properties from application.properties");
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            properties.load(in);
        } catch (FileNotFoundException e) {
            log.info("application.properties not found");
            log.debug("Failed to load application.properties", e);
        } catch (IOException e) {
            log.info("error occurs while reading application.properties", e);
            log.debug("Failed to load application.properties", e);
        }
    }

    public static String getProperty(String property) {
        return properties.getProperty(property);
    }

    public static Integer getPropertyAsInteger(String property) {
        return Integer.parseInt(properties.getProperty(property));
    }

}
