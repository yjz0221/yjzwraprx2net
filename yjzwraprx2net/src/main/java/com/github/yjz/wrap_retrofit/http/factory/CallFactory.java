package com.github.yjz.wrap_retrofit.http.factory;


import com.github.yjz.wrap_retrofit.YJZNetMgr;
import com.github.yjz.wrap_retrofit.util.NetRequestKeyUtils;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 作者:cl
 * 创建日期：2024/7/16
 * 描述:实现Url替换
 */
public class CallFactory implements Call.Factory {

    private final Call.Factory delegate;

    public CallFactory(Call.Factory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call newCall(Request request) {
        Request.Builder builder = request.newBuilder();
        String replaceUrl = request.header(NetRequestKeyUtils.HEADER_KEY_REPLACE_URL);

        if (replaceUrl == null || replaceUrl.isEmpty()) {
            return delegate.newCall(builder.build());
        } else {
            builder.removeHeader(NetRequestKeyUtils.HEADER_KEY_REPLACE_URL);

            String newUrl = request.url().toString().replace(YJZNetMgr.getBaseUrl(), replaceUrl);

            return delegate.newCall(builder.url(HttpUrl.get(newUrl)).build());
        }
    }
}
