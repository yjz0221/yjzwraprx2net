package com.github.yjz.wrap_retrofit.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * 作者:yjz
 * 创建日期：2025/7/19
 * 描述:用于在运行时以创建泛型类型
 */
public final class ParameterizedTypeImpl implements ParameterizedType {

   private final Type rawType;
   private final Type[] actualTypeArguments;
   private final Type ownerType;

   public ParameterizedTypeImpl(@NonNull Type rawType, @NonNull Type[] actualTypeArguments, @Nullable Type ownerType) {
      Objects.requireNonNull(rawType, "rawType == null");
      Objects.requireNonNull(actualTypeArguments, "actualTypeArguments == null");

      this.rawType = rawType;
      this.actualTypeArguments = actualTypeArguments.clone(); // 克隆数组以保证不可变性
      this.ownerType = ownerType;

      // 检查泛型参数的合法性
      for (Type typeArgument : this.actualTypeArguments) {
         Objects.requireNonNull(typeArgument, "typeArgument == null");
      }
   }

   @NonNull
   @Override
   public Type[] getActualTypeArguments() {
      // 返回数组的克隆，以保证不可变性
      return actualTypeArguments.clone();
   }

   @NonNull
   @Override
   public Type getRawType() {
      return rawType;
   }

   @Nullable
   @Override
   public Type getOwnerType() {
      return ownerType;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) return true;
      // 必须是 ParameterizedType 的实例才能比较
      if (!(other instanceof ParameterizedType)) return false;

      ParameterizedType that = (ParameterizedType) other;

      return Objects.equals(this.getRawType(), that.getRawType()) &&
              Objects.equals(this.getOwnerType(), that.getOwnerType()) &&
              Arrays.equals(this.getActualTypeArguments(), that.getActualTypeArguments());
   }

   @Override
   public int hashCode() {
      // 根据 equals 方法中使用的字段来计算 hashCode
      return Arrays.hashCode(actualTypeArguments)
              ^ Objects.hashCode(rawType)
              ^ Objects.hashCode(ownerType);
   }

   @NonNull
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(typeToString(rawType));

      if (actualTypeArguments.length > 0) {
         sb.append("<");
         for (int i = 0; i < actualTypeArguments.length; i++) {
            if (i > 0) {
               sb.append(", ");
            }
            sb.append(typeToString(actualTypeArguments[i]));
         }
         sb.append(">");
      }
      return sb.toString();
   }

   /**
    * 将 Type 转换为可读的字符串，处理 Class 和 ParameterizedType 的情况。
    */
   public static String typeToString(Type type) {
      return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
   }
}