package com.aohaitong.utils.base;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 实用工具类
 */
public class Utils {

    /**
     * 是否为空
     *
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            String instance = (String) obj;
            return instance.trim().length() <= 0 || "null".equalsIgnoreCase(instance);
        } else if (obj instanceof Integer) {
            Integer instance = (Integer) obj;
            return instance < 0;
        } else if (obj instanceof List<?>) {
            List<?> instance = (List<?>) obj;
            return instance.size() <= 0;
        } else if (obj instanceof Map<?, ?>) {
            Map<?, ?> instance = (Map<?, ?>) obj;
            return instance.size() <= 0;
        } else if (obj instanceof Object[]) {
            Object[] instance = (Object[]) obj;
            return instance.length <= 0;
        } else if (obj instanceof Long) {
            Long instance = (Long) obj;
            return instance < 0;
        }
        return false;
    }

    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static String myTrim(String s, char c) {
        int start = 0, end = s.length() - 1;

        while (start <= end && s.charAt(start) == c) {
            start++;
        }
        while (start <= end && s.charAt(end) == c) {
            end--;
        }
        return s.substring(start, end + 1);
    }

    public static int getSignValue(int value, int bit) {
        String binaryString = Integer.toBinaryString(value);
        //	int bit = ;
        if (binaryString.length() == bit) {
            int a2 = ~value;
            int a3 = a2 + 1;
            int result = Integer.parseInt(Integer.toBinaryString(a3).substring(32 - binaryString.length()), 2);
            return result * (-1);
        } else {
            return value;
        }

    }

    public static String fillStr(String value, int length, String fillStr) {
        while (value.length() < length) {
            value = fillStr + value;
        }
        return value;
    }

    public static int getNumberDecimalDigits(double number) {
        if (number == (long) number) {
            return 0;
        }
        int i = 0;
        BigDecimal bDouble = new BigDecimal(String.valueOf(number));
        BigDecimal value = new BigDecimal("10");
        while (true) {
            i++;
            bDouble = bDouble.multiply(value);
            if (new BigDecimal(bDouble.intValue()).compareTo(bDouble) == 0) {
                return i;
            }
        }
    }

    public static void main(String[] args) {

        Integer[] ids = {};
        System.out.println(isEmpty(ids));
    }
}
