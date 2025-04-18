package com.github.mylibdemo.api;


import android.util.Log;

import com.github.mylibdemo.bean.AuthBean;
import com.github.mylibdemo.bean.BaseResponse;
import com.github.mylibdemo.bean.LoginUserInfo;
import com.github.mylibdemo.net.RetrofitMgr;
import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.util.RxUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * 作者:cl
 * 创建日期：2024/11/20
 * 描述:测试
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
                .compose(RxUtils.applyApiResult());
    }


    public Call<BaseResponse<LoginUserInfo>> syncUserLogin() {

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("Token", "");
        paramMap.put("FunctionID", "UserLogin");
        paramMap.put("CheckNum", "13361B5D026A112440B3000AB9C1A599");
        paramMap.put("Params", "{\"UserName\": \"zhouguojian\",\"Password\": \"Zhougj@55331\"}");

        Log.e("DemoApiRequeset", "userLogin " + paramMap);

        return RetrofitMgr.getDemoApi()
                .syncUserLogin(paramMap);
    }

    public Observable<BaseResponse<LoginUserInfo>> userLogin() {

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("Token", "");
        paramMap.put("FunctionID", "UserLogin");
        paramMap.put("CheckNum", "13361B5D026A112440B3000AB9C1A599");
        paramMap.put("Params", "{\"UserName\": \"zhouguojian\",\"Password\": \"Zhougj@55331\"}");

        Log.e("DemoApiRequeset", "userLogin " + paramMap);

        return RetrofitMgr.getDemoApi()
                .useLogin(paramMap);
    }
}
