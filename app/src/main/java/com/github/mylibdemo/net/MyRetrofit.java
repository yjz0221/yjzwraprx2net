package com.github.mylibdemo.net;


import com.github.mylibdemo.net.adapter.IntegerTypeAdapter;
import com.github.mylibdemo.net.adapter.StringTypeAdapter;
import com.github.mylibdemo.net.factory.CustomGsonConverterFactory;
import com.github.mylibdemo.net.factory.DefaultCustomGsonConverterFactory;
import com.github.yjz.wrap_retrofit.YJZNetMgr;
import com.github.yjz.wrap_retrofit.http.factory.ApiResultResponseAdapterFactory;
import com.github.yjz.wrap_retrofit.http.factory.CallFactory;
import com.github.mylibdemo.net.util.NetConstant;
import com.github.yjz.wrap_retrofit.http.interceptor.LoggerInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
                .registerTypeAdapter(int.class, new IntegerTypeAdapter())
                .registerTypeAdapter(String.class, new StringTypeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(new CallFactory(okHttpClient))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(new ApiResultResponseAdapterFactory())
                .addConverterFactory(new DefaultCustomGsonConverterFactory(gson))
                .addConverterFactory(CustomGsonConverterFactory.create())
                .build();
    }


    private OkHttpClient initOkhttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new LoggerInterceptor(true))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    public <T> T createService(Class<T> cls) {
        return retrofit.create(cls);
    }


}
