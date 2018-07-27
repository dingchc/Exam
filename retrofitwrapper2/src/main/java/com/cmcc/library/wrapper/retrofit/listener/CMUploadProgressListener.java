package com.cmcc.library.wrapper.retrofit.listener;

/**
 * 上传Listener
 * @author Ding
 */
public interface CMUploadProgressListener {

    /**
     * 进度变化
     *
     * @param current 已下载大小
     * @param total   总共大小
     * @param done    是否完成
     */
    void progress(long current, long total, boolean done);
}