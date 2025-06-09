package com.github.yjz.wrap_retrofit.http;


import androidx.annotation.NonNull;

import com.github.yjz.wrap_retrofit.http.exception.ApiException;

/**
 * 作者:yjz
 * 创建日期：2024/11/19
 * 描述:封装返回结果
 */
public class ApiResult<T> {

    public static class Success<T> extends ApiResult<T> {
        public T data;
        public String msg;

        public Success(T t) {
            this(t, "");
        }

        public Success(T t, String msg) {
            this.data = t;
            this.msg = msg;
        }

        @NonNull
        @Override
        public String toString() {
            return "Success{" +
                    "data=" + data +
                    ", msg='" + msg + '\'' +
                    '}';
        }

        public static <T> ApiResult<T> create(T t, String msg){
            return (ApiResult<T>) new Success(t,msg);
        }

        public static <T> ApiResult<T> create(T t){
            return create(t,"");
        }
    }


    public static class BizError extends ApiResult<Object> {
        public int code;
        public String msg;

        private BizError(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }


        public static <T> ApiResult<T> create(int code, String msg){
            return (ApiResult<T>) new BizError(code,msg);
        }


        @NonNull
        @Override
        public String toString() {
            return "BizError{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }


    public static class Exception extends ApiResult<Object> {
        public ApiException exp;

        private Exception(ApiException t) {
            this.exp = t;
        }

        @NonNull
        @Override
        public String toString() {
            return "Exception{" +
                    "exp=" + exp + '\'' +
                    '}';
        }

        public static <T> ApiResult<T> create(ApiException t){
            return (ApiResult<T>) new Exception(t);
        }
    }


    public static class Progress extends ApiResult<Object> {
        public long totalLen;
        public long curLen;
        public String msg;


        private Progress(long totalLen, long curLen, String msg) {
            this.totalLen = totalLen;
            this.curLen = curLen;
            this.msg = msg;
        }


        public static <T> ApiResult<T> create(long totalLen, long curLen, String msg){
            return (ApiResult<T>) new Progress(totalLen,curLen,msg);
        }


        @NonNull
        @Override
        public String toString() {
            return "Progress{" +
                    "totalLen=" + totalLen +
                    ", curLen=" + curLen +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    public static class Loading extends ApiResult<Object> {
        public String msg;


        public Loading() {
            this("");
        }

        public Loading(String msg) {
            this.msg = msg;
        }


        public static <T> ApiResult<T> create(String msg){
            return (ApiResult<T>) new Loading(msg);
        }

        public static <T> ApiResult<T> create(){
            return create("");
        }
    }

    public boolean isLoading() {
        return (this instanceof ApiResult.Loading);
    }


    public boolean isSuccess() {
        return (this instanceof ApiResult.Success);
    }

    public boolean isBizError() {
        return (this instanceof ApiResult.BizError);
    }

    public boolean isException() {
        return (this instanceof ApiResult.Exception);
    }

    public boolean isProgress() {
        return (this instanceof ApiResult.Progress);
    }


    public ApiResult.Success<T> getApiSuccess() {
        return (ApiResult.Success<T>) this;
    }


    public ApiResult.BizError getApiBizError() {
        return (ApiResult.BizError) this;
    }

    public ApiResult.Progress getApiProgress() {
        return (ApiResult.Progress) this;
    }

    public ApiResult.Exception getApiException() {
        return (ApiResult.Exception) this;
    }


    public T getData() {
        if (isSuccess()) {
            ApiResult.Success<T> result = (Success<T>) this;

            return result.data;
        }

        return null;
    }

    public String getDataMessage() {
        if (isSuccess()) {
            ApiResult.Success<T> result = (Success<T>) this;

            return result.msg;
        }

        return null;
    }


}