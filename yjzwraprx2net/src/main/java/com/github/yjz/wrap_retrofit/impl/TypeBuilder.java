package com.github.yjz.wrap_retrofit.impl;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 作者:yjz
 * 创建日期：2025/7/19
 * 描述:创建一个 ParameterizedType，用于Gson等库的泛型解析
 */
public final class TypeBuilder {
   /**
    * 创建一个 ParameterizedType，用于 Gson 等库的泛型解析。
    * eg.OkHttpCallUtils.getResponse(httpClient,TypeBuilder.newInstance(BaseModel.class,UserModel.class))
    *
    * @param rawType       原始类型，例如 BaseModel.class
    * @param typeArguments 泛型参数数组，例如 UserModel.class
    * @return 构造好的 Type 对象
    */
   public static ParameterizedType newInstance(Type rawType, Type... typeArguments) {
      return new ParameterizedTypeImpl(rawType, typeArguments, null);
   }
}
