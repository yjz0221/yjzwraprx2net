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
 * 作者:yjz
 * 创建日期：2025/5/24
 * 描述:Okhttp请求结果转换工具
 */
object OkHttpCallUtils {


    inline fun <reified T> responseToApiResult(call: Call):ApiResult<T>{
        return responseToApiResult(call,object : TypeToken<T>(){}.type)
    }


    fun <T> responseToApiResult(call: Call,type: Type):ApiResult<T>{
        return responseToApiResult(call,type, block = {jsonString -> Gson().fromJson(jsonString,type) })
    }

    /**
     * 同步执行OkHttp Call，将响应转换为ApiResult<T>
     */
    fun <T> responseToApiResult(
        call: Call, type: Type,block: (jsonString: String) -> T = { json->
            Gson().fromJson(json, type)
        }
    ): ApiResult<T> {
        var response: Response? = null
        try {
            response = call.execute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val jsonString = responseBody.string()

                    //使用Gson将JSON字符串解析为T类型实体
                    val parsedData = block(jsonString)

                    if (parsedData is IBizError) {
                        //如果解析出的数据实现了IBizError接口，并且其表示业务错误
                        if (parsedData.isBizError()) {
                            return ApiResult.BizError.create(
                                parsedData.bizCode(),
                                parsedData.bizMsg()
                            )
                        }
                    }
                    //如果不是业务错误，则表示成功
                    return ApiResult.Success(parsedData)
                } else {
                    //HTTP成功但响应体为空。这可能是一个错误，也可能是服务器返回 204 No Content。
                    val emptyBodyException = ApiException(
                        -1,
                        YJZNetMgr.getString(R.string.yjz_net_http_success_but_response_body_empty)
                    )
                    return ApiResult.Exception.create(emptyBodyException)
                }
            } else {
                // HTTP 状态码不在 2xx 范围内
                val httpErrorWrapper = OkHttpErrorWrapper(
                    response.code(),
                    response.message(),
                    response.body()?.string()
                )
                return ApiResult.Exception.create(ApiException.parseException(httpErrorWrapper))
            }
        } catch (e: Exception) {
            return ApiResult.Exception.create(ApiException.parseException(e))
        } finally {
            response?.close()
        }
    }


    inline fun <reified T> getResponse(call: Call):T{
        return getResponse(call,object : TypeToken<T>(){}.type)
    }

    /**
     * 同步执行OkHttp Call，将响应转换为实体 T
     */
    fun <T> getResponse(
        call: Call, type: Type
    ): T{
        return getResponse(call,type, block = {jsonString -> Gson().fromJson(jsonString,type) })
    }

    /**
     * 同步执行OkHttp Call，将响应转换为实体 T
     */
    fun <T> getResponse(
        call: Call, type: Type,block: (jsonString: String) -> T = { json->
            Gson().fromJson(json, type)
        }
    ): T {
        var response: Response? = null
        try {
            response = call.execute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val jsonString = responseBody.string()

                    //使用Gson将JSON字符串解析为T类型实体
                    val parsedData = block(jsonString)
                    //如果不是业务错误，则表示成功
                    return parsedData
                } else {
                    //HTTP成功但响应体为空。这可能是一个错误，也可能是服务器返回 204 No Content。
                    val httpErrorWrapper = OkHttpErrorWrapper(
                        response.code(),
                        response.message(),
                        response.body()?.string()
                    )
                    throw httpErrorWrapper
                }
            } else {
                // HTTP 状态码不在 2xx 范围内
                val httpErrorWrapper = OkHttpErrorWrapper(
                    response.code(),
                    response.message(),
                    response.body()?.string()
                )
                throw httpErrorWrapper
            }
        } catch (e: Exception) {
            throw e
        }finally {
            response?.close()
        }
    }
}