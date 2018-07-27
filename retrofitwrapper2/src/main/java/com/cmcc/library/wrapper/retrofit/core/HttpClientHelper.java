package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.listener.DownloadProgressListener;
import com.cmcc.library.wrapper.retrofit.listener.UploadProgressListener;
import com.cmcc.library.wrapper.retrofit.util.MSAppLogger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by liupeng_a on 2017/1/10.
 */

public class HttpClientHelper {

    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;
    private static OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);

    static HttpLoggingInterceptor logger = new HttpLoggingInterceptor();

    /**
     * 普通请求
     *
     * @return
     */
    public static OkHttpClient getHttpClient() {


        builder.interceptors().clear();

        if (MSAppLogger.BuildConfig.DEBUG) {

            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (!builder.interceptors().contains(logger)) {
                builder.addInterceptor(logger);
            }
        }

        return builder.build();
    }

    /**
     * 下载文件
     *
     * @param listener       回调
     * @param isSupportRange 是否支持断点续传
     * @return
     */
    public static OkHttpClient getDownloadHttpClient(DownloadProgressListener listener, boolean isSupportRange) {

        builder.interceptors().clear();

        if (isSupportRange) {

            return builder
                    .addInterceptor(new DownloadRangeInterceptor(listener))
                    .build();

        } else {
            return builder
                    .addInterceptor(new DownloadInterceptor(listener))
                    .build();
        }

    }

    /**
     * 上传文件
     *
     * @param listener 回调
     * @return
     */
    public static OkHttpClient getUploadHttpClient(UploadProgressListener listener) {


        builder.interceptors().clear();

        return builder
                .addInterceptor(new UploadInterceptor(listener))
                .build();
    }
}
