package com.github.mylibdemo.net.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者:chenlong
 * 创建日期：2024/3/19
 * 描述: 电池包在线加载检测sp工具类
 */
public class SPUtils {

    private static final String FILE_NAME = "yjznet_demo_sp_file";


    private static SharedPreferences sharedPreferences;

    public static synchronized void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getInstance() {
        if (sharedPreferences == null) {
            throw new IllegalArgumentException("sharedPreferences 未初始化");
        }
        return sharedPreferences;
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String def) {
        return sharedPreferences.getString(key, def);
    }

    public static void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static Boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public static void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public static void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
