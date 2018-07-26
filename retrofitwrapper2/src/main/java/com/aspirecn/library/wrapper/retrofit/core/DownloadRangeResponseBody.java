package com.aspirecn.library.wrapper.retrofit.core;

import android.os.Looper;
import android.os.Message;


import com.aspirecn.library.wrapper.retrofit.model.MSProgressInfo;
import com.aspirecn.library.wrapper.retrofit.listener.DownloadProgressListener;
import com.aspirecn.library.wrapper.retrofit.util.MSUtil;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 支持断点下载的ResponseBody
 * Created by ding on 2017/4/10.
 */

public class DownloadRangeResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadProgressListener listener;
    private BufferedSource bufferedSource;
    private DownloadHandler downloadHandler;

    public DownloadRangeResponseBody(ResponseBody responseBody, DownloadProgressListener listener, boolean isSupportRange) {
        this.responseBody = responseBody;
        this.listener = listener;

        if (listener instanceof DownloadRangeImpl) {
            DownloadRangeImpl download = (DownloadRangeImpl) listener;
            download.setSupportRange(isSupportRange);
        }

        downloadHandler = new DownloadHandler(Looper.getMainLooper());
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {

            long current = 0L;
            long total = 0L;
            long lastNotifyTime = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {

                long byteRead = super.read(sink, byteCount);

                if (listener != null) {

                    if (total == 0) {
                        total = responseBody.contentLength();
                    }

                    current += byteRead != -1 ? byteRead : 0;

                    // 暂时不用了，因为这个是网络读取数据的百分比，UI显示的实际需要写入文件的百分比，不一样的
                    // notifyCallback(lastNotifyTime, current, total);
                }

                return byteRead;
            }
        };
    }

    /**
     * 通知回调
     * @param lastNotifyTime 上次通知时间
     * @param current 当前
     * @param total 全部
     */
    private void notifyCallback(long lastNotifyTime, long current, long total) {

        long currentTime = System.currentTimeMillis();

        long duration = currentTime - lastNotifyTime;

        if (lastNotifyTime <= 0 || duration >= 2000 || current == total) {


            // 设置下载完成
            if (listener instanceof DownloadRangeImpl) {

                DownloadRangeImpl downloadRangeImpl = ((DownloadRangeImpl) listener);

//                if (current == total) {
//                    downloadRangeImpl.setDownloadFinished(true);
//                }

                current += downloadRangeImpl.getDownloadSize();
                total += downloadRangeImpl.getDownloadSize();
            }

//                        MSAppLogger.i("percent=" + (current*1.0f/total*1.0f));

            sendProgressMessage(current, total);

//                        listener.progress(current, total, current == total);

            lastNotifyTime = currentTime;
        }
    }

    /**
     * 发送进度更新消息
     *
     * @param current 当前下载量
     * @param total   总量
     */
    private void sendProgressMessage(long current, long total) {

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


                        if (listener != null) {
                            listener.progress(current, total, current == total);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
