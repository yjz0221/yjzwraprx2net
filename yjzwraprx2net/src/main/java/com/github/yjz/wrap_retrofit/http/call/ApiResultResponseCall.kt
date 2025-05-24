package com.github.yjz.wrap_retrofit.http.call


import android.util.Log
import com.github.yjz.wrap_retrofit.http.ApiResult
import com.github.yjz.wrap_retrofit.http.exception.ApiException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.ParameterizedType


internal class ApiResultResponseCall(
    private val delegate: Call<Any>,
    private val wrapperType: ParameterizedType
) : Call<Any> {

    override fun enqueue(callback: Callback<Any>): Unit =
        delegate.enqueue(object : Callback<Any> { // 自定义Callback，响应成功/失败都回调Response.success
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (call.isCanceled) return // 请求取消

                if (response.isSuccessful) {
                    val body = response.body()
                    callback.onResponse(this@ApiResultResponseCall, Response.success(body))
                } else {
                    val exception = HttpException(response)
                    callback.onResponse(
                        this@ApiResultResponseCall,
                        Response.success(ApiResult.Exception.create<Any>(ApiException.parseException(exception)))
                    )
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                if (call.isCanceled) return // 请求取消

                callback.onResponse(this@ApiResultResponseCall, Response.success(ApiResult.Exception.create<Any>(ApiException.parseException(t))))
            }
        })

    override fun clone(): Call<Any> = ApiResultResponseCall(delegate, wrapperType)

    override fun execute(): Response<Any> =
        throw UnsupportedOperationException("${this.javaClass.name} doesn't support execute")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel(): Unit = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}