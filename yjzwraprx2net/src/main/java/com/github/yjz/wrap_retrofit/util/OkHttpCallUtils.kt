package com.github.yjz.wrap_retrofit.util

import com.github.yjz.wrap_retrofit.R
import com.github.yjz.wrap_retrofit.YJZNetMgr
import com.github.yjz.wrap_retrofit.http.ApiResult
import com.github.yjz.wrap_retrofit.http.exception.ApiException
import com.github.yjz.wrap_retrofit.http.exception.OkHttpErrorWrapper
import com.github.yjz.wrap_retrofit.listener.IBizError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Response
import java.lang.reflect.Type

/**
 * 作者:cl
 * 创建日期：2025/5/24
 * 描述:Okhttp请求结果转换工具
 */
object OkHttpCallUtils{


    /**
     * 仅Kotlin调用
     */
    inline fun <reified T> responseToApiResult(call: Call): ApiResult<T> {
        return responseToApiResult(call, object : TypeToken<T>() {}.type)
    }


    fun <T> responseToApiResult(call: Call, typeOfT: Type): ApiResult<T>{
        return responseToApiResult(call,typeOfT,Gson())
    }


    /**
    * 同步执行OkHttp Call，将响应转换为ApiResult<T>
    */
    fun <T> responseToApiResult(call: Call, typeOfT: Type,gson: Gson): ApiResult<T> {
        var response: Response? = null
        try {
            response = call.execute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val jsonString = responseBody.string()

                    // 使用 Gson 将 JSON 字符串解析为 T 类型实体
                    val parsedData = gson.fromJson<T>(jsonString, typeOfT)

                    if (parsedData is IBizError) {
                        // 如果解析出的数据实现了 IBizError 接口，并且其表示业务错误
                        if (parsedData.isBizError()) {
                            return ApiResult.BizError.create(parsedData.bizCode(), parsedData.bizMsg())
                        }
                    }
                    //如果不是业务错误，则表示成功
                    return ApiResult.Success(parsedData)
                } else {
                    //HTTP成功但响应体为空。这可能是一个错误，也可能是服务器返回 204 No Content。
                    val emptyBodyException = ApiException(-1, YJZNetMgr.getString(R.string.yjz_net_http_success_but_response_body_empty))
                    return ApiResult.Exception.create(emptyBodyException)
                }
            } else {
                // HTTP 状态码不在 2xx 范围内
                val httpErrorWrapper = OkHttpErrorWrapper(response.code(), response.message(),  response.body()?.string())
                return ApiResult.Exception.create(ApiException.parseException(httpErrorWrapper))
            }
        } catch (e: Exception) {
            return ApiResult.Exception.create(ApiException.parseException(e))
        } finally {
            response?.close()
        }
    }
}