package com.github.mylibdemo.net.factory;

import com.github.mylibdemo.bean.BaseResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者:cl
 * 创建日期：2025/1/11
 * 描述:接口业务异常时，不解析Value字段
 */
public class CustomGsonConverterFactory extends Converter.Factory {

    private final GsonConverterFactory gsonConverterFactory;
    private final Gson gson;

    private CustomGsonConverterFactory(Gson gson) {
        this.gson = gson;
        this.gsonConverterFactory = GsonConverterFactory.create(gson);
    }

    public static CustomGsonConverterFactory create() {
        return create(new Gson());
    }

    public static CustomGsonConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new CustomGsonConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter<ResponseBody, ?> gsonConverter = gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody value) throws IOException {
                String responseString = value.string();
                BaseResponse<?> baseResponse = gson.fromJson(responseString, BaseResponse.class); // 先解析 BaseResponse 部分
                if (0 != baseResponse.errNum) {
                    // ErrNum 不为 0，不解析 Value，直接返回 baseResponse，并抛出异常。
                    return baseResponse;
                } else {
                    // ErrNum 为 0，正常解析 Value
                    return gsonConverter.convert(ResponseBody.create(value.contentType(), responseString));
                }
            }
        };
    }
}
