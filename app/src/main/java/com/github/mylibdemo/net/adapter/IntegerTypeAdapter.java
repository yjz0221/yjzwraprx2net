package com.github.mylibdemo.net.adapter;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * 作者:yjz
 * 创建日期：2025/1/11
 * 描述:将服务器返回的空字符int转为默认值
 */
public class IntegerTypeAdapter extends TypeAdapter<Integer> {

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0; // 或者返回默认值，例如 0
        }
        try {
            String value = in.nextString();
            if ("".equals(value)) {
                return 0; // 空字符串时返回 0 或其他默认值
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new JsonParseException(e); // 抛出 JsonParseException 异常，方便统一处理
        }
    }
}
