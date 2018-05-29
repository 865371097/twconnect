package com.uas.dingzikaifa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseUtil {
    /**
     * 数组转化成字符串, null和空字符自动去掉
     *
     * @param arr
     *            待转化数组
     * @param ch
     *            分割符
     */
    public static String parseArray2Str(Object[] arr, String ch) {
        StringBuffer sb = new StringBuffer();
        for (Object s : arr) {
            if (s != null && !s.toString().trim().equals("")) {
                sb.append(s);
                sb.append(ch);
            }
        }
        if (sb.length() > 0 && ch.length() > 0) {
            return sb.substring(0, sb.lastIndexOf(ch));
        }
        return sb.toString();
    }

    public static String parseDateToString(Date date, String f) {
        if (f == null) {
            f = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(f);
        if (date == null) {
            date = new Date();
        }
        return sdf.format(date);
    }

    public static Date parseStringToDate(Object date, String f) {
        if (f == null) {
            f = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(f);
        if (date == null) {
            return new Date();
        }
        try {
            return sdf.parse(date.toString());
        } catch (ParseException e) {
            return new Date();
        }
    }
}
