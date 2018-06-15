package com.example.chen.hsms.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/28.
 */

public class StringUtils {
    public StringUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断字符串是否null
     */
    public static String stringNull(String text) {
        if (text == null) {
            return "";
        }
        return text;
    }
    /**
     * 判断字符串是否以,结尾，并去除
     */
    public static String stringSubEnds(String text) {
        if (text.endsWith(",")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
//   判断 List
    public static boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    public static boolean useSet(String[] arr, String targetValue) {
        Set<String> set = new HashSet<String>(Arrays.asList(arr));
        return set.contains(targetValue);
    }
}
