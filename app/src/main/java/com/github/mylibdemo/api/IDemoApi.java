package com.github.mylibdemo.api;

import com.github.mylibdemo.bean.AuthBean;
import com.github.mylibdemo.net.util.NetConstant;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 作者:cl
 * 创建日期：2024/11/20
 * 描述: 测试接口
 */
public interface IDemoApi {


    @FormUrlEncoded
    @POST("uaa/oauth/token")
    Observable<AuthBean> getAccessToken(@FieldMap Map<String, String> map);

}