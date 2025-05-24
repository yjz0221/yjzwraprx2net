package com.github.yjz.wrap_retrofit.http.factory


import com.github.yjz.wrap_retrofit.http.ApiResult
import com.github.yjz.wrap_retrofit.http.adapter.ApiResultResponseAdapter
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Retrofit结果转换器，在协程的基础上将结果转换成ApiResult<T>形式
 */
class ApiResultResponseAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // suspend 函数在 retrofit 中的返回值是 Call
        if (Call::class.java != getRawType(returnType)) return null

        // 检查返回类型是否是ParameterizedType
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<ApiResult<Foo>> or Call<ApiResult<out Foo>>"
        }

        // 获取Call内一层的泛型类型
        val responseType = getParameterUpperBound(0, returnType)

        // 如果非ApiResult不处理
        if (!(getRawType(responseType) === ApiResult::class.java)) return null

        return ApiResultResponseAdapter(responseType)
    }

}