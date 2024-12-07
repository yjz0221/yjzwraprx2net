package com.github.mylibdemo.api;


import com.github.mylibdemo.bean.AuthBean;
import com.github.mylibdemo.net.RetrofitMgr;
import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.util.RxUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * 作者:chenlong
 * 创建日期：2024/11/20
 * 描述:
 */
public class DemoApiRequest {


    public Observable<ApiResult<AuthBean>> requestAccessToken() {

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("username", "18689203203");
        paramMap.put("password", "123456");
        paramMap.put("grant_type", "password");
        paramMap.put("scope", "app");

        return RetrofitMgr.getDemoApi()
                .getAccessToken(paramMap)
                .compose(RxUtils.applyRxNet());
    }

}
