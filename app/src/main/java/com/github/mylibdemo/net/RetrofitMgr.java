package com.github.mylibdemo.net;

import android.annotation.SuppressLint;
import android.content.Context;

import com.github.mylibdemo.api.IDemoApi;
import com.github.mylibdemo.net.util.SPUtils;
import com.github.yjz.wrap_retrofit.YJZNetMgr;


public class RetrofitMgr {

    @SuppressLint("StaticFieldLeak")
    private static final RetrofitMgr sMgr = new RetrofitMgr();

    private Context context;
    private MyRetrofit myRetrofit;

    private RetrofitMgr() {

    }


    public static void init(Context context) {
        sMgr.context = context.getApplicationContext();

        YJZNetMgr.init(context);

        SPUtils.init(getCtx());
    }


    public static Context getCtx() {
        if (sMgr.context == null) throw new RuntimeException("context is null");

        return sMgr.context;
    }

    public static MyRetrofit getRetrofit() {
        return sMgr.getMyRetrofit();
    }

    public static IDemoApi getDemoApi() {
        return getRetrofit().createService(IDemoApi.class);
    }


    public static void release() {
        sMgr.context = null;
        sMgr.myRetrofit = null;
    }


    private MyRetrofit getMyRetrofit() {
        if (myRetrofit != null) return myRetrofit;

        synchronized (MyRetrofit.class) {
            if (myRetrofit == null) {
                myRetrofit = new MyRetrofit();
            }
        }

        return myRetrofit;
    }


}
