package com.github.mylibdemo.bean;

import androidx.annotation.NonNull;

import com.github.yjz.wrap_retrofit.util.OkHttpCallUtils;
import com.google.gson.annotations.SerializedName;

import kotlin.jvm.functions.Function1;

/**
 * 作者:yjz
 * 创建日期：2025/1/10
 * 描述:登入账号信息
 */
public class LoginUserInfo{

    @SerializedName("ID")
    private int id;
    @SerializedName("Token")
    private String loginToken = "";


    private String account = "";

    private String pwd = "";

    private boolean remember;//是否记住密码


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }


    @NonNull
    @Override
    public String toString() {
        return "LoginUserInfo{" +
                "id=" + id +
                ", loginToken='" + loginToken + '\'' +
                ", account='" + account + '\'' +
                ", pwd='" + pwd + '\'' +
                ", remember=" + remember +
                '}';
    }
}
