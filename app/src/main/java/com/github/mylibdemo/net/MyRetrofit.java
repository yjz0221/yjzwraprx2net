package com.github.mylibdemo.net;


import com.github.mylibdemo.BuildConfig;
import com.github.yjz.wrap_retrofit.YJZNetMgr;
import com.github.yjz.wrap_retrofit.http.factory.CallFactory;
import com.github.yjz.wrap_retrofit.http.interceptor.LoggerInterceptor;
import com.github.mylibdemo.net.util.NetConstant;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyRetrofit {

    private Retrofit retrofit;

    private final String baseUrl;


    public MyRetrofit() {
        this(NetConstant.BASE_URL);
    }

    public MyRetrofit(String baseUrl) {
        if (baseUrl == null) throw new NullPointerException("baseUrl is null");

        YJZNetMgr.setBaseUrl(baseUrl);

        this.baseUrl = baseUrl;

        initRetrofit(initOkhttpClient());
    }

    private void initRetrofit(OkHttpClient okHttpClient) {
        if (okHttpClient == null) throw new NullPointerException("okHttpClient is null");

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(new CallFactory((Call.Factory) okHttpClient))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    private OkHttpClient initOkhttpClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(BuildConfig.DEBUG))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    public <T> T createService(Class<T> cls) {
        return retrofit.create(cls);
    }


}
