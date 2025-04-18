package com.github.yjz.wrap_retrofit.util

import com.github.yjz.wrap_retrofit.http.ApiResult
import com.github.yjz.wrap_retrofit.http.exception.ApiException
import com.github.yjz.wrap_retrofit.listener.IBizError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * 作者:cl
 * 创建日期：2025/4/17
 * 描述:自定义Flow的扩展方法，将结果封装成ApiResult
 */
object FlowUtils {

    fun <T> Flow<T>.applySchedulers(): Flow<T> = this.flowOn(Dispatchers.IO)

    fun <T> Flow<T>.applyApiResult(): Flow<ApiResult<T>> =
        this.map { result ->
            if (result is IBizError && result.isBizError) {
                ApiResult.BizError.create<T>(result.bizCode(), result.bizMsg())
            } else {
                ApiResult.Success.create(result)
            }
        }.catch { throwable ->
            emit(ApiResult.Exception.create(ApiException.parseException(throwable)))
        }


    fun <T> Flow<T>.applyApiResultSchedulers(): Flow<ApiResult<T>> = this.applySchedulers().applyApiResult()
}