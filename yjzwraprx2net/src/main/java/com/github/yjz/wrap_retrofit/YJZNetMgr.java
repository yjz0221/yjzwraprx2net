package com.github.yjz.wrap_retrofit;

import android.annotation.SuppressLint;
import android.content.Context;

public class YJZNetMgr {

    @SuppressLint("StaticFieldLeak")
    private static final YJZNetMgr sYJZNetMgr = new YJZNetMgr();

    private Context ctx;

    private String baseUrl;


    public static void init(Context context) {
        if (context == null) throw new IllegalArgumentException("上下文参数为空");

        sYJZNetMgr.ctx = context.getApplicationContext();
    }


    public static void setBaseUrl(String baseUrl) {
        sYJZNetMgr.baseUrl = baseUrl;
    }

    public static String getBaseUrl() {
        if (sYJZNetMgr.baseUrl == null) throw new IllegalArgumentException("baseUrl参数为空");

        return sYJZNetMgr.baseUrl;
    }


    public static Context getCtx(){
        if (sYJZNetMgr.ctx == null) throw new IllegalArgumentException("上下文参数为空");

        return sYJZNetMgr.ctx;
    }

    public static String getString(int resId){
        return getCtx().getString(resId);
    }

}
