package com.github.yjz.wrap_retrofit.http.callback;


import com.github.yjz.wrap_retrofit.http.exception.ApiException;
import com.github.yjz.wrap_retrofit.http.exception.BizException;
import com.github.yjz.wrap_retrofit.listener.IBizError;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;


public abstract class WrapCallback<R> implements Callback<R> {

    public abstract void onSuccess(R result);

    public abstract void onBizError(BizException bizException);

    public abstract void onException(Throwable t);

    @Override
    public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response) {

        if (response.isSuccessful()) {
            R body = response.body();

            if (body instanceof IBizError) {
                //{"success":false,"msg":"菜单级数无效","data":null,"result":null,"code":0,"token":null}

                IBizError bizError = (IBizError) body;

                if (bizError.isBizError()) {
                    onBizError(new BizException(bizError.bizCode(), bizError.bizMsg()));
                    return;
                }
            }

            onSuccess(response.body());
        } else {
            onException(ApiException.parseException(new HttpException(response)));
        }
    }

    @Override
    public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
        onException(ApiException.parseException(t));
    }
}
