package com.github.yjz.wrap_retrofit.util;


import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.http.exception.ApiException;
import com.github.yjz.wrap_retrofit.listener.IBizError;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者:cl
 * 创建日期：2024/11/19
 * 描述: 基于RxJava流转换
 */
public class RxUtils {


    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    public static <T> ObservableTransformer<T, ApiResult<T>> applyApiResultSchedulers() {
        return new ObservableTransformer<T, ApiResult<T>>() {
            @Override
            public ObservableSource<ApiResult<T>> apply(Observable<T> upstream) {
                return upstream.compose(applySchedulers()).compose(applyApiResult());
            }
        };
    }

    public static <T> ObservableTransformer<T, ApiResult<T>> applyApiResult() {
        return new ObservableTransformer<T, ApiResult<T>>() {
            @Override
            public ObservableSource<ApiResult<T>> apply(Observable<T> upstream) {
                return upstream.map(new Function<T, ApiResult<T>>() {
                    @Override
                    public ApiResult<T> apply(T t) throws Exception {
                        if (t instanceof IBizError) {
                            IBizError iBizError = (IBizError) t;

                            if (iBizError.isBizError()) {
                                return ApiResult.BizError.create(iBizError.bizCode(), iBizError.bizMsg());
                            }
                        }

                        return new ApiResult.Success<>(t);
                    }
                }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends ApiResult<T>>>() {
                    @Override
                    public ObservableSource<? extends ApiResult<T>> apply(Throwable throwable) throws Exception {
                        return Observable.just(ApiResult.Exception.create(ApiException.parseException(throwable)));
                    }
                });
            }
        };
    }


}
