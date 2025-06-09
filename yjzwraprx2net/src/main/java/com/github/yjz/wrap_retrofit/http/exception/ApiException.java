package com.github.yjz.wrap_retrofit.http.exception;

import androidx.annotation.NonNull;

import com.github.yjz.wrap_retrofit.R;
import com.github.yjz.wrap_retrofit.YJZNetMgr;
import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import retrofit2.HttpException;

/**
 * 作者:yjz
 * 创建日期：2024/11/19
 * 描述:统一异常解析类
 */
public class ApiException extends Exception {


    public int code;
    public String message;
    public int httpCode = ERROR.HttpCode.UNKNOWN;


    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public ApiException(int code, String message) {
        this(new RuntimeException(message), code);
        this.message = message;
    }


    public static ApiException parseException(Throwable e) {
        ApiException backException;

        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;

            backException = new ApiException(e, ERROR.HTTP_ERROR);
            backException.message = httpCode2String(httpException.code());
            backException.httpCode = netErrorCode2HttpCode(httpException.code());
        }else if (e instanceof  OkHttpErrorWrapper){
            OkHttpErrorWrapper httpErrorWrapper = (OkHttpErrorWrapper) e;

            backException = new ApiException(e, ERROR.HTTP_ERROR); // 使用原始的 OkHttpErrorWrapper
            backException.message = httpCode2String(httpErrorWrapper.getHttpCode());
            backException.httpCode = netErrorCode2HttpCode(httpErrorWrapper.getHttpCode());
        }else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            backException = new ApiException(e, ERROR.PARSE_ERROR);
            backException.message = YJZNetMgr.getString(R.string.yjz_net_parse_error); // 提示数据解析错误

        } else if (e instanceof ConnectException) {
            backException = new ApiException(e, ERROR.NETWORD_ERROR);
            backException.message = YJZNetMgr.getString(R.string.yjz_net_connect_error); // 提示无法连接到服务器，请检查网络

        } else if (e instanceof UnknownHostException) {
            backException = new ApiException(e, ERROR.NETWORD_ERROR);
            backException.message = YJZNetMgr.getString(R.string.yjz_net_unknown_host); // 提示无法找到指定服务器，请检查网络或服务器地址
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            backException = new ApiException(e, ERROR.SSL_ERROR);
            backException.message = YJZNetMgr.getString(R.string.yjz_net_ssl_error); // 提示安全连接失败，可能是证书问题

        } else if (e instanceof ConnectTimeoutException) {
            backException = new ApiException(e, ERROR.TIMEOUT_ERROR);
            backException.message = YJZNetMgr.getString(R.string.yjz_net_connect_timeout); // 提示连接服务器超时

        } else if (e instanceof java.net.SocketTimeoutException) {
            backException = new ApiException(e, ERROR.TIMEOUT_ERROR);
            backException.message = YJZNetMgr.getString(R.string.yjz_net_socket_timeout); // 提示请求超时，请稍后重试

        } else {
            backException = new ApiException(e, ERROR.UNKNOWN);
            String realExceptionMessage = e.getLocalizedMessage();
            if (realExceptionMessage != null){
                backException.message = YJZNetMgr.getString(R.string.yjz_net_unknown)+"：\n"+realExceptionMessage; // 提示未知错误，请稍后重试
            }else{
                backException.message = YJZNetMgr.getString(R.string.yjz_net_unknown) + "：\n"+ e;
            }
        }

        return backException;

    }


    /**
     * 重写 toString() 方法，提供更详细的异常信息。
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // 1. 添加异常类名 (包含包路径)
        sb.append(getClass().getName());

        // 2. 添加自定义的 code
        sb.append(" (Code: ").append(code).append(")");

        // 3. 添加自定义的 message
        if (message != null && !message.isEmpty()) {
            sb.append(": ").append(message);
        }

        // 4. 添加原始的 cause (如果存在)
        Throwable cause = getCause();
        if (cause != null) {
            sb.append(" [Caused by: ").append(cause.toString()).append("]");
        }

        return sb.toString();
    }

    public static int netErrorCode2HttpCode(int httpCode){
        int resultCode = ERROR.HttpCode.UNKNOWN;
        switch (httpCode){
            case ERROR.HttpCode.UNAUTHORIZED:
            case ERROR.HttpCode.FORBIDDEN:
            case ERROR.HttpCode.NOT_FOUND:
            case ERROR.HttpCode.METHOD_ALLOWED:
            case ERROR.HttpCode.REQUEST_TIMEOUT:
            case ERROR.HttpCode.GATEWAY_TIMEOUT:
            case ERROR.HttpCode.INTERNAL_SERVER_ERROR:
            case ERROR.HttpCode.BAD_GATEWAY:
            case ERROR.HttpCode.SERVICE_UNAVAILABLE:
                resultCode = httpCode;
                break;
        }

        return resultCode;
    }

    public static String httpCode2String(int httpCode){
        String message = "";
        switch (httpCode) {
            case ERROR.HttpCode.UNAUTHORIZED:
                message = YJZNetMgr.getString(R.string.yjz_net_unauthorized); // 提示未授权或登录过期
                break;
            case ERROR.HttpCode.FORBIDDEN:
                message = YJZNetMgr.getString(R.string.yjz_net_forbidden); // 提示没有权限
                break;
            case ERROR.HttpCode.NOT_FOUND:
                message = YJZNetMgr.getString(R.string.yjz_net_not_found); // 提示请求的资源不存在
                break;
            case ERROR.HttpCode.METHOD_ALLOWED:
                message = YJZNetMgr.getString(R.string.yjz_net_method_allowed); //方法不被允许
                break;
            case ERROR.HttpCode.REQUEST_TIMEOUT:
                message = YJZNetMgr.getString(R.string.yjz_net_request_timeout); // 提示请求超时
                break;
            case ERROR.HttpCode.GATEWAY_TIMEOUT:
                message = YJZNetMgr.getString(R.string.yjz_net_gateway_timeout); // 提示网关超时
                break;
            case ERROR.HttpCode.INTERNAL_SERVER_ERROR:
                message = YJZNetMgr.getString(R.string.yjz_net_internal_server_error); // 提示服务器内部错误
                break;
            case ERROR.HttpCode.BAD_GATEWAY:
                message = YJZNetMgr.getString(R.string.yjz_net_bad_gateway); // 提示无效的网关
                break;
            case ERROR.HttpCode.SERVICE_UNAVAILABLE:
                message = YJZNetMgr.getString(R.string.yjz_net_service_unavailable); // 提示服务不可用
                break;
            default:
                message = YJZNetMgr.getString(R.string.yjz_net_http_error_default) + " (" + httpCode + ")"; // 提示服务器返回错误，并显示具体状态码
                break;
        }

        return message;
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;


        public static class HttpCode{
            public static final int UNKNOWN = -1;
            public static final int UNAUTHORIZED = 401;
            public static final int FORBIDDEN = 403;
            public static final int NOT_FOUND = 404;
            public static final int METHOD_ALLOWED = 405;
            public static final int REQUEST_TIMEOUT = 408;
            public static final int INTERNAL_SERVER_ERROR = 500;
            public static final int BAD_GATEWAY = 502;
            public static final int SERVICE_UNAVAILABLE = 503;
            public static final int GATEWAY_TIMEOUT = 504;
        }

    }
}
