package com.uas.dingzikaifa.util;

import org.apache.commons.codec.digest.DigestUtils;

public class SecurityUtil {
    public static boolean checkToken(String token) {
        if (!StringUtils.isEmpty(token)) {
            String str = "dingzikaifa" + "20180528";
            String md5 = DigestUtils.md5Hex(str);
            System.out.println("md5:" + md5);
            if (md5.equals(token)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
