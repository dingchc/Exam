package com.aspirecn.library.wrapper.retrofit.core;

/**
 * 下载异常
 * Created by ding on 4/21/17.
 */

public class MSHttpException extends Exception {

    public final static int CODE_DEFAULT = 0;
    public final static int CODE_REQUEST_CANCELED = 1001; //请求取消
    public final static int CODE_REQUEST_INTERCEPTED = 1002; //请求中断

    private int code = CODE_DEFAULT;

    public MSHttpException(String msg) {
        super(msg);
    }

    public MSHttpException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
