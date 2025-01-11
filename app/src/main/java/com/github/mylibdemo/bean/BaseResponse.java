package com.github.mylibdemo.bean;

import androidx.annotation.NonNull;

import com.github.yjz.wrap_retrofit.listener.IBizError;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者:cl
 * 创建日期：2025/1/11
 * 描述:网络请求基类
 */
public class BaseResponse<T> implements Serializable, IBizError {

    @SerializedName("ErrNum")
    public int errNum;

    @SerializedName("ErrMsg")
    public String errMsg;

    @SerializedName("PageIndex")
    public String pageIndex;

    @SerializedName("PageCount")
    public int pageCount;

    @SerializedName("Value")
    public T value;


    @NonNull
    @Override
    public String toString() {
        return "BaseResponse{" +
                "errNum='" + errNum + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", pageIndex='" + pageIndex + '\'' +
                ", pageCount='" + pageCount + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public int bizCode() {
        return errNum;
    }

    @Override
    public String bizMsg() {
        return errMsg;
    }

    @Override
    public boolean isBizError() {
        return errNum != 0;
    }
}