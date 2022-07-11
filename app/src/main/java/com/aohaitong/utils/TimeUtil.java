package com.aohaitong.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String stampToDateSec(String s) {
        if (s.length() == 10) {
            s += "000";
        }
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.parseLong(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDateMin(String s) {
        if (s.length() == 10) {
            s += "000";
        }
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = Long.parseLong(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    //如果是当天就不显示日期
    public static String stampToDateMinWithOutDay(String s) {
        if (s.length() == 10) {
            s += "000";
        }
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = Long.parseLong(s);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(lt);
        Date old = null;
        Date now = null;
        try {
            old = sdf.parse(sdf.format(date));
            now = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long oldTime = old.getTime();
        long nowTime = now.getTime();
        long day = (nowTime - oldTime) / (24 * 60 * 60 * 1000);
        if (day < 1) {  //今天
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        } else if (day == 1) {     //昨天
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return "昨天 " + format.format(date);
        } else {    //可依次类推
            return simpleDateFormat.format(date);
        }
    }


}
