package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.listener.CMDownloadProgressListener;
import com.cmcc.library.wrapper.retrofit.listener.CMUploadProgressListener;
import com.cmcc.library.wrapper.retrofit.util.CMAppLogger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * OKHttpClient生成类
 * @author Ding
 */

public class CMHttpClientHelper {

    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;
    private static OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);

    private static HttpLoggingInterceptor logger = new HttpLoggingInterceptor();

    /**
     * 普通请求
     *
     * @return OkHttpClient
     */
    static OkHttpClient getHttpClient() {


        builder.interceptors().clear();

        if (CMAppLogger.BuildConfig.DEBUG) {

            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (!builder.interceptors().contains(logger)) {
                builder.addInterceptor(logger);
            }
        }

        return builder.build();
    }

    /**
     * 下载文件, 支持断点续传
     *
     * @param listener 回调
     * @return OkHttpClient
     */
    static OkHttpClient getDownloadHttpClient(CMDownloadProgressListener listener) {

        builder.interceptors().clear();

        return builder
                .addInterceptor(new CMDownloadRangeInterceptor(listener))
                .build();


    }

    /**
     * 上传文件
     *
     * @param listener 回调
     * @return OkHttpClient
     */
    public static OkHttpClient getUploadHttpClient(CMUploadProgressListener listener) {


        builder.interceptors().clear();

        return builder
                .addInterceptor(new CMUploadInterceptor(listener))
                .build();
    }
}