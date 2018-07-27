package com.cmcc.library.wrapper.retrofit.listener;

/**
 * Created by Ding on 3/7/17.
 */

public interface HttpProgressCallback extends HttpCallback {

    void progress(long current, long total, boolean done);
}
