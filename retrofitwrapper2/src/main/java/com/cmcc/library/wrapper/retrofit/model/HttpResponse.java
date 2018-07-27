package com.cmcc.library.wrapper.retrofit.model;

/**
 * Created by ding on 3/1/17.
 */

/**
 * 返回类
 * @param <T>
 */
public class HttpResponse<T> {
    public int ret = 0;
    public String msg = "";
    public T data;
}
