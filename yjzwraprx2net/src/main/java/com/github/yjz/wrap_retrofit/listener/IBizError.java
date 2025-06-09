package com.github.yjz.wrap_retrofit.listener;

/**
 * 作者:yjz
 * 创建日期：2024/11/19
 * 描述:业务异常接口，实体实现此接口判断是否为业务异常
 */
public interface IBizError {

    int bizCode();

    String bizMsg();

    boolean isBizError();
}
