package com.github.mylibdemo.net.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * 作者:cl
 * 创建日期：2025/1/11
 * 描述:将服务器返回的null字符串转为空字符
 */
public class StringTypeAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public String read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return ""; // null 值时返回空字符串
        }
        try {
            String value = in.nextString();
            if (value == null || "null".equalsIgnoreCase(value)) { // 增加对"null"字符串的处理
                return ""; // "null" 字符串也返回空字符串
            }
            return value;
        } catch (IllegalStateException e) { // 处理类型不匹配的情况
            in.skipValue(); // 跳过该值，避免解析崩溃
            return "";
        }
    }
}
