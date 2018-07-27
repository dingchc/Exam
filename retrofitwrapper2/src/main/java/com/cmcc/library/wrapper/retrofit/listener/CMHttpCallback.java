package com.cmcc.library.wrapper.retrofit.listener;

import com.cmcc.library.wrapper.retrofit.model.CMBaseResponse;

/**
 * 接口
 *
 * @author ding
 */
public interface CMHttpCallback {

    /**
     * 成功
     *
     * @param response 解析的结构
     * @param json     json字符串
     */
    void onSuccess(CMBaseResponse response, String json);

    /**
     * 异常
     *
     * @param exception 异常
     */
    void onException(Throwable exception);
}
