package com.leosam.tvbox.mv.utils;

/**
 * @author admin
 * @since 2023/6/12 21:32
 */
public class StringUtils {

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
