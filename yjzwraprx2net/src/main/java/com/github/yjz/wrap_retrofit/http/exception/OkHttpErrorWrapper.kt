package com.github.yjz.wrap_retrofit.http.exception

import okhttp3.Response

/**
 * 作者:cl
 * 创建日期：2025/5/24
 * 描述:
 */
class OkHttpErrorWrapper(
    val httpCode: Int,
    val httpMessage: String,
    val rawResponseBody: String? = null
) : Exception(httpMessage)