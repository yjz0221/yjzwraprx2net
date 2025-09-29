package com.github.yjz.wrap_retrofit.http.factory


import com.github.yjz.wrap_retrofit.http.ApiResult
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 作者:yjz
 * 创建日期：2025/9/29
 * 描述: 一个高度灵活的抽象工厂，用于将服务器响应转换为 ApiResult<T>
 *     它不限制服务器返回的JSON结构，使用者通过实现 unwrapResponse 方法来定义
 *     如何解析他们自己的JSON结构并将其映射到 ApiResult
 */
abstract class ApiResultConverterFactory protected constructor(
    protected val gson: Gson // 构造函数接收一个Gson实例，供子类使用
) : Converter.Factory() {

    // 内部使用的 Gson 转换器工厂，用于委托解析 ApiResult 内部的实际数据类型 (例如 BaseResponse<T>)
    protected val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    /**
     * 【使用者需要实现的唯一核心方法】
     * 在此方法中，使用者需要：
     * 1. 使用 Gson 将 responseBody 解析成他们自己定义的服务器响应模型 (e.g., MyBaseResponse<T>)。
     * 2. 根据他们模型的业务逻辑（如 code, status 等）进行判断。
     * 3. 返回一个 ApiResult.Success 或 ApiResult.Failure。
     *
     * @param responseBody Retrofit 传来的原始 ResponseBody。
     * @param type ApiResult<T> 中的泛型 T 的具体类型 (e.g., User::class.java)。
     * @return 返回一个 ApiResult<*> 的实例。
     */
    // 【修改点1】将抽象方法的返回类型修改为 ApiResult<*>
    abstract fun unwrapResponse(responseBody: ResponseBody, type: Type,delegateConverter: Converter<ResponseBody, *>): ApiResult<*>

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        // 1. 检查外层返回类型是否是 ApiResult
        if (getRawType(type) != ApiResult::class.java) {
            return null
        }

        // 2. 确保 ApiResult 是参数化类型, e.g., ApiResult<User>
        if (type !is ParameterizedType) {
            throw IllegalArgumentException("ApiResult return type must be parameterized (e.g., ApiResult<User>)")
        }

        // 3. 获取 ApiResult 内部的泛型 T，例如 User
        val apiResultInnerType = getParameterUpperBound(0, type)

        // 4. 获取用于解析 R 的原始 GsonConverter (例如 BaseResponse<LoginUserInfo>)
        val delegateConverter: Converter<ResponseBody, *> =
            gsonConverterFactory.responseBodyConverter(type, annotations, retrofit)
                ?: return null // 如果无法获取委托转换器，则返回 null

        // 5. 返回我们自定义的转换器
        return ApiResultConverter(apiResultInnerType,delegateConverter)
    }

    /**
     * 【修改点3】移除内部转换器的泛型 <T>，并使其返回 ApiResult<*>
     */
    private inner class ApiResultConverter(
        private val type: Type,
        private val delegateConverter: Converter<ResponseBody, *>
    ) : Converter<ResponseBody, ApiResult<*>> {

        override fun convert(value: ResponseBody): ApiResult<*> {
            return unwrapResponse(value, type,delegateConverter)
        }
    }
}