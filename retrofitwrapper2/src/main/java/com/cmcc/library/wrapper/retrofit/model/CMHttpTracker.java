package com.cmcc.library.wrapper.retrofit.model;

/**
 * 请求追踪者
 *
 * @author Ding
 *         Created by ding on 3/9/17.
 */

public class CMHttpTracker {

    private String url;

    public CMHttpTracker(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
