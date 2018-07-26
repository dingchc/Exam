package com.aspirecn.library.wrapper.retrofit.core;

import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.aspirecn.library.wrapper.retrofit.model.MSProgressInfo;
import com.aspirecn.library.wrapper.retrofit.listener.DownloadProgressListener;
import com.aspirecn.library.wrapper.retrofit.listener.HttpProgressCallback;
import com.aspirecn.library.wrapper.retrofit.util.MSDirUtil;
import com.aspirecn.library.wrapper.retrofit.util.MSUtil;


/**
 * 断点下载辅助类
 * Created by ding on 4/20/17.
 */

public class DownloadRangeImpl implements DownloadProgressListener {

    private HttpProgressCallback callback;
    private boolean isSupportRange; //是否支持断点续传
    private String destPath; //存储的文件名
    private String tempPath; //临时文件名
    private long downloadSize = 0; //已下载的大小
    private DownloadHandler downloadHandler;
    private long lastNotifyTime; // 上次通知回调的时间

    public DownloadRangeImpl(String url, HttpProgressCallback callback) {
        this.callback = callback;

        destPath = createDestPath(url);
        tempPath = createTempPath(url);

        downloadHandler = new DownloadHandler(Looper.getMainLooper());
    }

    /**
     * 生成输出文件路径
     *
     * @param url 文件下载地址
     * @return 输出文件路径
     */
    public static String createDestPath(String url) {

        String filePath = "";

        if (TextUtils.isEmpty(url)) {
            return filePath;
        }

        String fileName = MSUtil.getFileName(url);
        filePath = MSDirUtil.getValidPath(MSDirUtil.getDownloadDir(), fileName);

        return filePath;
    }

    /**
     * 生成临时文件路径
     *
     * @param url 文件下载地址
     * @return 临时文件路径
     */
    public static String createTempPath(String url) {

        String filePath = "";

        if (TextUtils.isEmpty(url)) {
            return filePath;
        }

        String fileName = MSUtil.createTempFileName(MSUtil.getFileName(url));
        filePath = MSDirUtil.getValidPath(MSDirUtil.getDownloadDir(), fileName);

        return filePath;
    }

    @Override
    public void progress(long current, long total, boolean done) {

//        MSAppLogger.i("current=" + current + ", total=" + total);

        notifyCallbackDelay(current, total);
    }

    /**
     * 通知回调
     * @param current 当前
     * @param total 全部
     */
    private void notifyCallbackDelay(long current, long total) {

        long currentTime = System.currentTimeMillis();

        long duration = currentTime - lastNotifyTime;

        if (lastNotifyTime <= 0 || duration >= 200 || current == total) {

            if (MSUtil.checkObjNotNull(callback)) {
                callback.progress(current, total, current == total);
            }
            lastNotifyTime = System.currentTimeMillis();
        }
    }

    /**
     * 是否支持断点续传
     *
     * @return true 支持、false 不支持
     */
    public synchronized boolean isSupportRange() {
        return isSupportRange;
    }

    /**
     * 设置是否支持
     *
     * @param supportRange 是、否
     */
    public synchronized void setSupportRange(boolean supportRange) {
        isSupportRange = supportRange;
    }

    /**
     * 获取保存的文件地址
     *
     * @return 保存的文件
     */
    public String getDestPath() {
        return destPath;
    }

    /**
     * 获取临时文件地址
     * @return
     */
    public String getTempPath() {
        return tempPath;
    }

    /**
     * 已下载的大小
     * @return 下载的大小
     */
    public long getDownloadSize() {
        return downloadSize;
    }

    /**
     * 设置已下载的大小
     * @param downloadSize 文件大小
     */
    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    /**
     * 发送进度更新消息
     * @param current 当前下载量
     * @param total 总量
     */
    public void sendProgressMessage(long current, long total) {

        MSProgressInfo progressInfo = new MSProgressInfo(current, total);

        Message msg = Message.obtain();
        msg.what = DownloadHandler.WHAT_UPDATE;
        msg.obj = progressInfo;

        downloadHandler.sendMessage(msg);
    }

    /**
     * 下载Handler
     */
    class DownloadHandler extends android.os.Handler {

        public static final int WHAT_UPDATE = 0;

        public DownloadHandler(Looper looper) {

            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            try {

                if (msg.what == WHAT_UPDATE) {

                    if (MSUtil.checkObjNotNull(msg.obj)) {

                        MSProgressInfo progressInfo = (MSProgressInfo) msg.obj;

                        long total = progressInfo.total;
                        long current = progressInfo.current;

                        progress(current, total, current == total);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
