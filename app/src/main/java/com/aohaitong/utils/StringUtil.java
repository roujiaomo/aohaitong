package com.aohaitong.utils;

import android.text.TextUtils;

public class StringUtil {

    /**
     * 获取第一个不为空的string
     *
     * @param strings
     * @return
     */
    public static String getFirstNotNullString(String[] strings) {
        for (String string : strings) {
            if (!TextUtils.isEmpty(string) && !"null".equals(string))
                return string;
        }
        return "";
    }
}
