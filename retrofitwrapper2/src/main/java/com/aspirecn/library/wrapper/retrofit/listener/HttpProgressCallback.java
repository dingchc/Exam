package com.aspirecn.library.wrapper.retrofit.listener;

import com.aspirecn.library.wrapper.retrofit.listener.HttpCallback;

/**
 * Created by Ding on 3/7/17.
 */

public interface HttpProgressCallback extends HttpCallback {

    void progress(long current, long total, boolean done);
}
