package com.aspirecn.library.wrapper.retrofit.listener;

/**
 * Created by liupeng_a on 2017/1/10.
 */

public interface UploadProgressListener {
    void progress(long current, long total, boolean done);
}
