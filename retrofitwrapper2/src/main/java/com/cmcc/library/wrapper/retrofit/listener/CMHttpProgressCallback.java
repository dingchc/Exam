package com.cmcc.library.wrapper.retrofit.listener;

/**
 * 进度变化Listener
 *
 * @author Ding
 *         Created by Ding on 3/7/17.
 */

public interface CMHttpProgressCallback extends CMHttpCallback {

    /**
     * 进度变化
     *
     * @param current 已下载大小
     * @param total   总共大小
     * @param done    是否完成
     */
    void progress(long current, long total, boolean done);
}
