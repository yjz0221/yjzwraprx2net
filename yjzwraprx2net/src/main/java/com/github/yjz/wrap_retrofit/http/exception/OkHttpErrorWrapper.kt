package com.github.yjz.wrap_retrofit.http.exception



class OkHttpErrorWrapper(
    val httpCode: Int,
    val httpMessage: String,
    val rawResponseBody: String? = null
) : Exception(httpMessage)