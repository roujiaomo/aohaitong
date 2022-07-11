package com.aohaitong.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//用于自身在后台同步时间,有自己的时间的时候不使用系统时间
public class DateUtil {
    private long currentTime;
    private static DateUtil dateUtil;

    public static DateUtil getInstance() {
        if (dateUtil == null) {
            dateUtil = new DateUtil();
        }
        return dateUtil;
    }

    private DateUtil() {
        currentTime = System.currentTimeMillis();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentTime += 1000;
            }
        }).start();
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * 获取时间
     *
     * @return 时间戳
     */
    public long getTime() {
        return currentTime;
    }

    /**
     * 获取时间
     *
     * @return 时间戳
     */
    public long getTime(int year, int month, int day, int hour, int minute, int second) {
        return dateToStamp(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
    }

    /*
     * 将时间转换为时间戳
     */
    public long dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            return ts;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0l;
        }
    }

    public int getYear() {
        String date = timeStamp2Date();
        return Integer.parseInt(date.split("-")[0]);
    }

    public int getMouth() {
        String date = timeStamp2Date();
        return Integer.parseInt(date.split("-")[1]);
    }

    public int getDay() {
        String date = timeStamp2Date();
        return Integer.parseInt(date.split("-")[2].split(" ")[0]);
    }

    public int getHour() {
        String date = timeStamp2Date();
        return Integer.parseInt(date.split(" ")[1].split(":")[0]);
    }

    public int getMinute() {
        String date = timeStamp2Date();
        return Integer.parseInt(date.split(" ")[1].split(":")[1]);
    }

    public int getSecond() {
        String date = timeStamp2Date();
        return Integer.parseInt(date.split(" ")[1].split(":")[2]);
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @return
     */
    public String timeStamp2Date() {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(currentTime));
    }
}
