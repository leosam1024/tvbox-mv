package com.leosam.tvbox.mv.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @since 2023/6/12 21:32
 */
public class StringUtils {

    public static final String EMPTY = "";

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String defaultIfEmpty(final String str, String defaultStr) {
        return isNotEmpty(str) ? str : defaultStr;
    }

    public static String trimToEmpty(final String str) {
        return str == null ? EMPTY : str.trim();
    }

    public static List<String> reverseSplit(String s, String delimiter, int limit) {
        List<String> result = new ArrayList<>();
        if (s == null || delimiter == null || limit <= 0) {
            return result;
        }

        int lastIndex = s.lastIndexOf(delimiter);
        while (limit > 1 && lastIndex != -1) {
            result.add(0, s.substring(lastIndex + delimiter.length()));
            s = s.substring(0, lastIndex);
            lastIndex = s.lastIndexOf(delimiter);
            limit--;
        }
        result.add(0, s);
        return result;
    }

    public static String cleanString(String s) {
        s = s.replace('（', '(')
                .replace('）', ')')
                .replace('/', '_')
                .replace(']', '_')
        ;
        // 去除乱码 以ISO8859-1方式读取UTF-8编码的中文
        if (s.contains("ç") ||
                s.contains("å") ||
                s.contains("ğ") ||
                s.contains("è") ||
                s.contains("â") ||
                s.contains("Š") ||
                s.contains("ã") ||
                s.contains("é") ||
                s.contains("æ") ||
                s.contains("§") ||
                s.contains("ï")) {
            s = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return s;
    }

}
