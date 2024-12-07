package com.github.yjz.wrap_retrofit.http.exception;

/**
 * 作者:cl
 * 创建日期：2024/11/19
 * 描述:业务异常
 */
public class BizException extends ApiException{

   public BizException(int code, String message) {
      super(code, message);
   }
}
