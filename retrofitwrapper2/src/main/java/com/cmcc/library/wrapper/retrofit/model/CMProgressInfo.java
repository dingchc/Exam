package com.cmcc.library.wrapper.retrofit.model;

/**
 * @author Ding
 * 进度类
 */
public class CMProgressInfo {

    public long current;
    public long total;

    public CMProgressInfo(long current, long total) {

        this.current = current;
        this.total = total;
    }
}
