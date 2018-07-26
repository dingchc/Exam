package com.aspirecn.library.wrapper.retrofit.listener;

/**
 * Created by ding on 3/1/17.
 */


import com.aspirecn.library.wrapper.retrofit.model.MSBaseResponse;

/**
 * 接口
 */
public interface HttpCallback {

    /**
     * 成功
     * @param response 解析的结构
     * @param json json字符串
     */
    void onSuccess(MSBaseResponse response, String json);

    void onException(Throwable e);
}
