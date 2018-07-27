package com.cmcc.library.wrapper.retrofit;

import android.animation.ValueAnimator;
import android.os.Looper;
import android.os.Message;


import com.cmcc.library.wrapper.retrofit.listener.UploadProgressListener;
import com.cmcc.library.wrapper.retrofit.model.MSProgressInfo;
import com.cmcc.library.wrapper.retrofit.util.MSAppLogger;
import com.cmcc.library.wrapper.retrofit.util.MSUtil;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by liupeng_a on 2017/1/10.
 */

public class UploadRequestBody extends RequestBody {

    private RequestBody requestBody;
    private UploadProgressListener listener;
    private BufferedSink bufferedSink;
    private UploadHandler progressHandler;

    public UploadRequestBody(RequestBody requestBody, UploadProgressListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        progressHandler = new UploadHandler(Looper.getMainLooper());
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }

        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {

        MSAppLogger.i(MSAppLogger.TAG1, "sink");

        return new ForwardingSink(sink) {
            long current = 0L;
            long total = 0L;

            long lastNotifyTime = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                if (listener != null) {

                    if (total == 0) {
                        total = contentLength();
                    }

                    current += byteCount;

                    long currentTime = System.currentTimeMillis();
                    long duration = currentTime - lastNotifyTime;

                    if (lastNotifyTime <= 0 || duration >= 10 || current == total) {

                        MSAppLogger.i(MSAppLogger.TAG1, "current=" + current + ", total=" + total);

                        MSProgressInfo progressInfo = new MSProgressInfo(current, total);

                        Message msg = Message.obtain();
                        msg.what = UploadHandler.WHAT_UPDATE;
                        msg.obj = progressInfo;

                        progressHandler.sendMessage(msg);

                        lastNotifyTime = currentTime;
                    }

                }
            }
        };
    }

    /**
     * 上传Handler
     */
    class UploadHandler extends android.os.Handler {

        private final int MAX_WAIT_TIME = 0; //等待次数
        private final int ANIMATOR_DURATION = 500; //动画时长
        public static final int WHAT_UPDATE = 0;

        private int updateTimes = 0;
        private long lastTimeMillis;


        public UploadHandler(Looper looper) {

            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            try {

                if (msg.what == WHAT_UPDATE) {

                    updateTimes++;

                    if (MSUtil.checkObjNotNull(msg.obj)) {

                        MSProgressInfo progressInfo = (MSProgressInfo) msg.obj;

                        long total = progressInfo.total;
                        long current = progressInfo.current;

                        // 如果上传byte次数很少就已经上传完了，那么启动动画
                        if (updateTimes < MAX_WAIT_TIME && current == total) {

                            animatorProgress();

                        } else if (updateTimes >= MAX_WAIT_TIME) {

                            if (listener != null) {
                                listener.progress(current, total, current == total);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * 由于上传时间太短，采用动画效果触发上传进度显示
         */
        private void animatorProgress() {

            ValueAnimator anim = ValueAnimator.ofInt(0, 100);
            anim.setDuration(ANIMATOR_DURATION);

            lastTimeMillis = System.currentTimeMillis();

            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    long currentTimeMillis = System.currentTimeMillis();
                    int current = (int) animation.getAnimatedValue();

                    if (currentTimeMillis - lastTimeMillis > 30 || current == 100) {

                        if (listener != null) {
                            listener.progress(current, 100, current == 100);
                        }

                        lastTimeMillis = currentTimeMillis;
                    }
                }
            });


            anim.start();
        }
    }
}
