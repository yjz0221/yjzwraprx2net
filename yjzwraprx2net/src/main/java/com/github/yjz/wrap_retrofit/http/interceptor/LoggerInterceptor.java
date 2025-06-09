package com.github.yjz.wrap_retrofit.http.interceptor;

import android.text.TextUtils;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者:yjz
 * 创建日期：2024/11/20
 * 描述:简易okhttp日志打印
 */
public class LoggerInterceptor implements Interceptor {

    private final static String LOG_TAG = "LoggerInterceptor";

    private Boolean openLog = false;

    private final String tag;



    public LoggerInterceptor(boolean openLog, String tag) {
        this.openLog = openLog;
        this.tag = tag;
    }


    public LoggerInterceptor(boolean openLog) {
        this(openLog, LOG_TAG);
    }

    public LoggerInterceptor() {
        this(false, LOG_TAG);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long endTime = System.currentTimeMillis();

        if (openLog) {
            MediaType contentType = null;
            String contentBody = "";
            int responseCode = response.code();

            if (supportPrintResponse(response)) {

                if (response.body() != null) {
                    contentType = response.body().contentType();
                    contentBody = response.body().string();
                }

                printLog(request, responseCode,contentBody, endTime - startTime);

                if (contentType != null) {

                    return response.newBuilder().body(ResponseBody.create(contentType, contentBody))
                            .build();

                    //okhttp高版本 3.12以上
//                    return response.newBuilder().body(ResponseBody.create(contentBody, contentType))
//                            .build();
                }
            }else if(supportPrintRequest(request)){
                //仅打印请求
                printLog(request, responseCode,contentBody, endTime - startTime);
            }
        }

        return response;
    }


    private String getRequestContentType(Request request){
        String contentType = request.header("Content-Type");
        if (TextUtils.isEmpty(contentType)){
            RequestBody requestBody = request.body();
            if (requestBody != null){
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null){
                    contentType = mediaType.toString();
                }
            }
        }
        return contentType;
    }

    private String getResponseContentType(Response response){
        String contentType = response.header("Content-Type");
        if (TextUtils.isEmpty(contentType)){
            ResponseBody body = response.body();
            if (body != null){
                MediaType mediaType = body.contentType();
                if (mediaType != null){
                    contentType = mediaType.toString();
                }
            }
        }
        return contentType;
    }


    private boolean supportPrintRequest(Request request) {
        return supportPrintContentType(getRequestContentType(request));
    }

    private boolean supportPrintResponse(Response response) {
        return supportPrintContentType(getResponseContentType(response));
    }


    private boolean supportPrintContentType(String contentType){
        if (!TextUtils.isEmpty(contentType)) {
            List<String> supportPrintType = new ArrayList<>();

            supportPrintType.add("text/*");
            supportPrintType.add("application/json");
            supportPrintType.add("application/xml");
            supportPrintType.add("application/javascript");
            supportPrintType.add("application/x-www-form-urlencoded");

            for (String supportType : supportPrintType) {
                if (contentType.startsWith(supportType)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void printLog(Request request, int responseCode, String responseContent, Long useTime) {
        String requestHeadersContent = "";

        StringBuilder headerStrBuilder = new StringBuilder();
        headerStrBuilder.append("{");

        for (int index = 0; index < request.headers().size(); index++) {
            headerStrBuilder
                    .append("\"")
                    .append(request.headers().name(index))
                    .append("\"")
                    .append(":")
                    .append("\"")
                    .append(request.headers().value(index))
                    .append("\"")
                    .append(",");
        }

        if (request.headers().size() > 0 && !TextUtils.isEmpty(headerStrBuilder)) {
            headerStrBuilder.delete(headerStrBuilder.length() - 1, headerStrBuilder.length());
        }else{
            String reqContentType = getRequestContentType(request);
            if (!TextUtils.isEmpty(reqContentType)){
                headerStrBuilder
                        .append("\"ContentType\"")
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(reqContentType)
                        .append("\"");
            }
        }

        //okhttp高版本 4.x
//        for (Iterator<Pair<String, String>> it = request.headers().iterator(); it.hasNext(); ) {
//            Pair<String, String> header = it.next();
//
//            headerStrBuilder
//                    .append("\"")
//                    .append(header.getFirst())
//                    .append("\"")
//                    .append(":")
//                    .append("\"")
//                    .append(header.getSecond())
//                    .append("\"")
//                    .append(",");
//
//            if (!TextUtils.isEmpty(headerStrBuilder)) {
//                headerStrBuilder.delete(headerStrBuilder.length() - 1, headerStrBuilder.length());
//            }
//        }

        headerStrBuilder.append("}");
        requestHeadersContent = headerStrBuilder.toString();


        String requestParamsContent = "";
        StringBuilder requestParamStrBuilder = new StringBuilder();
        RequestBody requestBody = request.body();

        requestParamStrBuilder.append("{");
        if (requestBody != null) {
            if (requestBody instanceof FormBody) {
                FormBody formBody = (FormBody) requestBody;

                for (int index = 0; index < formBody.size(); index++) {
                    requestParamStrBuilder
                            .append("\"")
                            .append(formBody.encodedName(index))
                            .append("\"")
                            .append(":")
                            .append(formBody.encodedValue(index))
                            .append(",");
                }

                if (formBody.size() >0 && !TextUtils.isEmpty(requestParamStrBuilder)) {
                    requestParamStrBuilder.delete(requestParamStrBuilder.length() - 1, requestParamStrBuilder.length());
                }

            }
        }

        requestParamStrBuilder.append("}");

        requestParamsContent = requestParamStrBuilder.toString();

        log2Console(
                tag,
                useTime,
                request.method() + ",url = " + request.url(),
                requestHeadersContent,
                requestParamsContent,
                responseCode,
                responseContent
        );
    }


    private synchronized static void log2Console(String tag, long useTime, String reqLog, String reqHeaders, String reqParams,int responseCode, String responseContent) {
        Log.d(tag, "\n");
        Log.d(tag, "╔═════════════════════════════════════════start request══════════════════════════════════════════════");
        Log.d(tag, "| Request: " + "method = " + formatJson(reqLog));
        Log.d(tag, "| Request headers: " + formatJson(reqHeaders));
        Log.d(tag, "| Request params: " + formatJson(reqParams));
        Log.d(tag, "| Response code: " + responseCode);
        Log.d(tag, "| Response: " + formatJson(responseContent));
        Log.d(tag, "╚═════════════════════════════════════════end request" + useTime + "════════════════════════════════════════════");
    }

    private static String formatJson(String msg) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (Exception e) {
            message = msg;
        }

        return message;
    }
}
