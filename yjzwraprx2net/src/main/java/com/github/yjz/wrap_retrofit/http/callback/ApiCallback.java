package com.github.yjz.wrap_retrofit.http.callback;

import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.http.exception.ApiException;
import com.github.yjz.wrap_retrofit.http.exception.BizException;


public abstract class ApiCallback<R> extends WrapCallback<R> {

    public abstract void onApiResponse(ApiResult<R> result);

    @Override
    public void onSuccess(R result) {
        onApiResponse(new ApiResult.Success<R>(result));
    }

    @Override
    public void onBizError(BizException bizException) {
        onApiResponse((ApiResult<R>) new ApiResult.BizError(bizException.code, bizException.message));
    }

    @Override
    public void onException(Throwable t) {
        onApiResponse((ApiResult<R>) new ApiResult.Exception(ApiException.parseException(t)));
    }
}
