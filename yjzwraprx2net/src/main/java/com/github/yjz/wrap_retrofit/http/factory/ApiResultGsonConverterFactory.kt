package com.github.yjz.wrap_retrofit.http.factory

import com.github.yjz.wrap_retrofit.http.ApiResult
import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 作者:yjz
 * 创建日期：2025/5/24
 * 描述: 用于生产 ApiResult<T> 转换器的抽象工厂。
 * 子类需要实现 [createApiResultConverter] 方法来定义具体的解析逻辑。
 */
@Deprecated("please use ApiResultConverterFactory")
abstract class ApiResultGsonConverterFactory protected constructor(protected val gson: Gson) : Converter.Factory() {

    // 内部使用的 Gson 转换器工厂，用于委托解析 ApiResult 内部的实际数据类型 (例如 BaseResponse<T>)
    protected val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    /**
     * 子类需要实现此方法，来定义具体的 ResponseBody 到 ApiResult<*> 的转换逻辑。
     *
     * @param actualResponseType ApiResult 内部的实际类型 (例如 BaseResponse<LoginUserInfo>)
     * @param delegateConverter 用于解析 actualResponseType 的委托转换器
     * @return 返回一个 Converter<ResponseBody, ApiResult<*>> 的具体实现
     */
    protected abstract fun createApiResultConverter(
        actualResponseType: Type,
        delegateConverter: Converter<ResponseBody, *>
    ): Converter<ResponseBody, ApiResult<*>>?

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {

        // 1. 检查返回类型是否是 ApiResult
        if (getRawType(type) != ApiResult::class.java) {
            // 如果不是 ApiResult，则交给 Retrofit 的默认 GsonConverterFactory 处理
            return gsonConverterFactory.responseBodyConverter(type, annotations, retrofit)
        }

        // 2. 确保 ApiResult 是参数化类型 (例如 ApiResult<Foo>)
        if (type !is ParameterizedType) {
            throw IllegalArgumentException("ApiResult return type must be parameterized (e.g., ApiResult<Foo>)")
        }

        // 3. 获取 ApiResult 内部的实际类型参数，例如 ApiResult<R> 中的 R (R 就是 BaseResponse<LoginUserInfo>)
        val actualResponseType = getParameterUpperBound(0, type)

        // 4. 获取用于解析 R 的原始 GsonConverter (例如 BaseResponse<LoginUserInfo>)
        val delegateConverter: Converter<ResponseBody, *> =
            gsonConverterFactory.responseBodyConverter(actualResponseType, annotations, retrofit)
                ?: return null // 如果无法获取委托转换器，则返回 null

        // 5. 调用抽象方法，让子类提供具体的 ApiResult 转换器实现
        return createApiResultConverter(actualResponseType, delegateConverter)
    }
}