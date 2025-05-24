package com.github.yjz.wrap_retrofit.http.adapter

import com.github.yjz.wrap_retrofit.http.call.ApiResultResponseCall
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class ApiResultResponseAdapter(private val successType: Type) : CallAdapter<Any, Call<Any>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<Any>): Call<Any> =
        ApiResultResponseCall(call, successType as ParameterizedType)

}