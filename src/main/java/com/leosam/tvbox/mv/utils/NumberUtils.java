package com.leosam.tvbox.mv.utils;

/**
 * @author admin
 * @since 2023/6/10 19:24
 */
public class NumberUtils {

    public static int toInt(final String str, final int defaultValue) {
        if (str == null || str.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

}
