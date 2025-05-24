package com.github.mylibdemo.net.factory

import com.github.mylibdemo.bean.BaseResponse

import com.github.yjz.wrap_retrofit.http.ApiResult
import com.github.yjz.wrap_retrofit.http.exception.ApiException
import com.github.yjz.wrap_retrofit.http.factory.ApiResultGsonConverterFactory
import com.google.gson.Gson
import com.google.gson.JsonElement

import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

/**
 * 作者:cl
 * 创建日期：2025/5/24
 * 描述:针对某个服务器的Json转ApiResult解析逻辑 (ErrNum, ErrMsg)
 */
class DefaultCustomGsonConverterFactory(gson: Gson) : ApiResultGsonConverterFactory(gson) {

    override fun createApiResultConverter(
        actualResponseType: Type,
        delegateConverter: Converter<ResponseBody, *>
    ): Converter<ResponseBody, ApiResult<*>>? {

        // 返回一个匿名对象，作为具体的 ResponseBody 到 ApiResult<*> 的转换器
        return object : Converter<ResponseBody, ApiResult<*>> {
            override fun convert(value: ResponseBody): ApiResult<*> {
                var responseString: String? = null
                try {
                    responseString = value.string() // 消耗 ResponseBody，获取响应字符串
                    val jsonElement: JsonElement? = gson.fromJson(responseString, JsonElement::class.java)

                    if (jsonElement == null || !jsonElement.isJsonObject) {
                        // 如果响应不是一个有效的 JSON 对象，视为解析失败
                        return ApiResult.Exception.create<Any>(
                            ApiException.parseException(RuntimeException("Response is not a valid JSON object or is empty."))
                        )
                    }

                    val jsonObject = jsonElement.asJsonObject
                    var errNum = -1 // 默认值
                    var errMsg = "Unknown Error"

                    // 尝试解析 ErrNum
                    if (jsonObject.has("ErrNum") && jsonObject.get("ErrNum").isJsonPrimitive) {
                        val errNumPrimitive = jsonObject.get("ErrNum").asJsonPrimitive
                        errNum = try {
                            errNumPrimitive.asInt // 尝试作为 Int 解析
                        } catch (e: NumberFormatException) {
                            try {
                                errNumPrimitive.asString.toInt() // 尝试作为 String 再转 Int
                            } catch (ex: NumberFormatException) {
                                // 无法解析 ErrNum，视为错误
                                return ApiResult.Exception.create<Any>(
                                    ApiException.parseException(RuntimeException("Cannot parse ErrNum as integer or string."))
                                )
                            }
                        }
                    }

                    // 尝试解析 ErrMsg
                    if (jsonObject.has("ErrMsg") && jsonObject.get("ErrMsg").isJsonPrimitive) {
                        errMsg = jsonObject.get("ErrMsg").asString
                    }

                    if (errNum != 0) {
                        // ErrNum 不为 0，表示业务错误
                        return ApiResult.BizError.create<Any>(errNum, errMsg)
                    } else {
                        // ErrNum 为 0，表示业务成功，委托给原始转换器解析 BaseResponse<T>
                        // 必须重新创建 ResponseBody，因为原始的 responseString 已经被 consumed
                        val newBody = ResponseBody.create(value.contentType(), responseString)
                        val body = delegateConverter.convert(newBody) // delegateConverter 返回的是 actualResponseType (例如 BaseResponse<LoginUserInfo>)

                        // 检查并返回 ApiResult.Success
                        if (body is BaseResponse<*>) { // 使用 Kotlin 的 'is' 进行类型检查和智能转换
                            return ApiResult.Success(body)
                        } else {
                            // 实际返回类型与 BaseResponse<T> 不符，这通常不应该发生
                            return ApiResult.Exception.create<Any>(
                                ApiException.parseException(
                                    RuntimeException("Delegate converter returned unexpected type: ${body?.javaClass?.name ?: "null"}")
                                )
                            )
                        }
                    }
                }catch (e: Exception) {
                    // 捕获所有其他未知异常
                    return ApiResult.Exception.create<Any>(ApiException.parseException(e))
                } finally {
                    value.close()
                }
            }
        }
    }
}