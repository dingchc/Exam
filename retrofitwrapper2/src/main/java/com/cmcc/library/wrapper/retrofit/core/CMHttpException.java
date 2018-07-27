package com.cmcc.library.wrapper.retrofit.core;

/**
 * 下载异常
 *
 * @author Ding
 *         Created by ding on 4/21/17.
 */

public class CMHttpException extends Exception {

    public final static int CODE_DEFAULT = 0;

    /**
     * 请求取消
     */
    public final static int CODE_REQUEST_CANCELED = 1001;

    /**
     * 请求中断
     */
    public final static int CODE_REQUEST_INTERCEPTED = 1002;


    private int code = CODE_DEFAULT;

    public CMHttpException(String msg) {
        super(msg);
    }

    public CMHttpException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
