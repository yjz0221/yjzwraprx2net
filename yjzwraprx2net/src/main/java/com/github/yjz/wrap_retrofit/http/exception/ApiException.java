package com.github.yjz.wrap_retrofit.http.exception;

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
 * 作者:cl
 * 创建日期：2024/11/19
 * 描述:统一异常解析类
 */
public class ApiException extends Exception {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public int code;
    public String message;


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

            switch (httpException.code()) {
                case UNAUTHORIZED:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_unauthorized); // 提示未授权或登录过期
                    break;
                case FORBIDDEN:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_forbidden); // 提示没有权限
                    break;
                case NOT_FOUND:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_not_found); // 提示请求的资源不存在
                    break;
                case REQUEST_TIMEOUT:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_request_timeout); // 提示请求超时
                    break;
                case GATEWAY_TIMEOUT:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_gateway_timeout); // 提示网关超时
                    break;
                case INTERNAL_SERVER_ERROR:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_internal_server_error); // 提示服务器内部错误
                    break;
                case BAD_GATEWAY:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_bad_gateway); // 提示无效的网关
                    break;
                case SERVICE_UNAVAILABLE:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_service_unavailable); // 提示服务不可用
                    break;
                default:
                    backException.message = YJZNetMgr.getString(R.string.yjz_net_http_error_default) + " (" + httpException.code() + ")"; // 提示服务器返回错误，并显示具体状态码
                    break;
            }

        } else if (e instanceof JsonParseException
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
            backException.message = YJZNetMgr.getString(R.string.yjz_net_unknown); // 提示未知错误，请稍后重试
        }

        return backException;

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

    }
}
