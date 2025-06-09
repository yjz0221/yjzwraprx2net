package com.github.mylibdemo.api;

import com.github.mylibdemo.bean.AuthBean;
import com.github.mylibdemo.bean.BaseResponse;
import com.github.mylibdemo.bean.LoginUserInfo;
import com.github.mylibdemo.net.util.NetConstant;
import com.github.yjz.wrap_retrofit.http.ApiResult

import io.reactivex.Observable;
import kotlinx.coroutines.flow.Flow;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 作者:yjz
 * 创建日期：2024/11/20
 * 描述: 测试接口
 */
interface IDemoApi {


    @FormUrlEncoded
    @POST("uaa/oauth/token")
    fun getAccessToken(@FieldMap map: Map<String, String>): Observable<AuthBean>

    @FormUrlEncoded
    @POST("OBDService.asmx/RequestData")
    fun useLogin(@FieldMap map: Map<String, String>): Observable<BaseResponse<LoginUserInfo>>


    @FormUrlEncoded
    @POST("OBDService.asmx/RequestData")
    suspend fun flowUserLogin1(@FieldMap map: Map<String, String>): ApiResult<BaseResponse<LoginUserInfo>>

    @FormUrlEncoded
    @POST("OBDService.asmx/RequestData")
    suspend fun flowUserLogin(@FieldMap map: Map<String, String>): BaseResponse<LoginUserInfo>

}
