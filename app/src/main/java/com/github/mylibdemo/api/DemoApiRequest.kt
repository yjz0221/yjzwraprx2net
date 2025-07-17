package com.github.mylibdemo.api;


import com.github.mylibdemo.BuildConfig
import com.github.mylibdemo.bean.BaseResponse;
import com.github.mylibdemo.bean.LoginUserInfo;
import com.github.mylibdemo.net.RetrofitMgr;
import com.github.mylibdemo.net.util.NetConstant;
import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.http.interceptor.LoggerInterceptor
import com.github.yjz.wrap_retrofit.util.OkHttpCallUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.flowOf
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 作者:yjz
 * 创建日期：2024/11/20
 * 描述:测试
 */
class DemoApiRequest {

    suspend fun flowUserLogin1(): Flow<ApiResult<BaseResponse<LoginUserInfo>>> {
        return flowOf(RetrofitMgr.getDemoApi().flowUserLogin1(createLoginParam()))
    }

    suspend fun flowUserLogin(): Flow<BaseResponse<LoginUserInfo>> {
        return flowOf(RetrofitMgr.getDemoApi().flowUserLogin(createLoginParam()))
    }


    fun userLogin(): Observable<BaseResponse<LoginUserInfo>> {
        return RetrofitMgr.getDemoApi().useLogin(createLoginParam())
    }


    fun okCallUserLogin(): ApiResult<BaseResponse<LoginUserInfo>> {
        val client = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor(BuildConfig.DEBUG))
            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
            .build()

        val formBody = FormBody.Builder()
            .add("Token", "")
            .add("FunctionID", "UserLogin")
            .add("CheckNum", "13361B5D026A112440B3000AB9C1A599")
            .add("Params", "{\"UserName\": \"zhouguojian\",\"Password\": \"Zhougj@55331\"}")
            .build()

        val request = Request.Builder()
            .url(NetConstant.BASE_URL + "OBDService.asmx/RequestData")
            .post(formBody)
            .build()

        return OkHttpCallUtils.responseToApiResult(call = client.newCall(request))
    }


    fun okCallUserLogin2(): BaseResponse<LoginUserInfo> {
        val client = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor(BuildConfig.DEBUG))
            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
            .build()

        val formBody = FormBody.Builder()
            .add("Token", "")
            .add("FunctionID", "UserLogin")
            .add("CheckNum", "13361B5D026A112440B3000AB9C1A599")
            .add("Params", "{\"UserName\": \"zhouguojian\",\"Password\": \"Zhougj@55331\"}")
            .build()

        val request = Request.Builder()
            .url(NetConstant.BASE_URL + "OBDService.asmx/RequestData")
            .post(formBody)
            .build()

        return OkHttpCallUtils.getResponse(call = client.newCall(request))
    }

    private fun createLoginParam() = hashMapOf<String,String>().apply {
        put("Token", "")
        put("FunctionID", "UserLogin")
        put("CheckNum", "13361B5D026A112440B3000AB9C1A599")
        put("Params", "{\"UserName\": \"zhouguojian\",\"Password\": \"Zhougj@55331\"}")
    }
}
