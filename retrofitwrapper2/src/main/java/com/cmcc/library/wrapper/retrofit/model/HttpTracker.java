package com.cmcc.library.wrapper.retrofit.model;

/**
 * 请求追踪者
 * Created by ding on 3/9/17.
 */

public class HttpTracker {

    private String url;

    public HttpTracker(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
