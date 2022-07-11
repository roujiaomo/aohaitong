package com.aohaitong.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.aohaitong.MyApplication;
import com.aohaitong.constant.CommonConstant;

/**
 * SharedPreferences工具类
 */
public class SPUtil {
    public static SPUtil instance;
    private final SharedPreferences sharedPreferences;

    static {
        instance = new SPUtil();
    }

    private SPUtil() {
        sharedPreferences = MyApplication.getContext().getSharedPreferences(CommonConstant.SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 存储数据
     * 这里要对存储的数据进行判断在存储
     * 只能存储简单的几种数据
     * 这里使用的是自定义的ContentValue类，来进行对多个数据的处理
     */
    //创建一个内部类使用，里面有key和value这两个值
    public static class ContentValue {
        String key;
        Object value;

        //通过构造方法来传入key和value
        public ContentValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    //一次可以传入多个ContentValue对象的值
    public void putValues(ContentValue... contentValues) {
        //获取SharePreference对象的编辑对象，才能进行数据的存储
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //数据分类和存储
        for (ContentValue contentValue : contentValues) {
            //如果是字符型类型
            if (contentValue.value instanceof String) {
                editor.putString(contentValue.key, contentValue.value.toString()).apply();
            }
            //如果是int类型
            if (contentValue.value instanceof Integer) {
                editor.putInt(contentValue.key, Integer.parseInt(contentValue.value.toString())).apply();
            }
            //如果是Long类型
            if (contentValue.value instanceof Long) {
                editor.putLong(contentValue.key, Long.parseLong(contentValue.value.toString())).apply();
            }
            //如果是布尔类型
            if (contentValue.value instanceof Boolean) {
                editor.putBoolean(contentValue.key, Boolean.parseBoolean(contentValue.value.toString())).apply();
            }
        }
    }

    //获取数据的方法
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, true);
    }

    public boolean getBooleanDefaultFalse(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    //清除当前文件的所有的数据
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
