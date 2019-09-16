package com.revoult.moneytransferapi.utils;

import org.slf4j.helpers.MessageFormatter;

public final class StringUtils {


    /**
     * This function replaces the placeholders in the provided string with the given args.
     *
     * @param string String with placeholders
     * @param args   Placeholder values.
     * @return formatted string
     */
    public static String formatString(String string, Object... args) {
        return MessageFormatter.arrayFormat(string, args).getMessage();
    }
}
