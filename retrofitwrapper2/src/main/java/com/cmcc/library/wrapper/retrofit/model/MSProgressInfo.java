package com.cmcc.library.wrapper.retrofit.model;

/**
 * 进度类
 */
public class MSProgressInfo {

    public long current;
    public long total;

    public MSProgressInfo(long current, long total) {

        this.current = current;
        this.total = total;
    }
}
