package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.listener.DownloadProgressListener;
import com.cmcc.library.wrapper.retrofit.listener.UploadProgressListener;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit封装类
 *
 * @author Ding
 *         Created by ding on 12/23/16.
 */

public class RetrofitClient {

    private Retrofit.Builder builder;

    private RetrofitClient() {
        builder = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public static RetrofitClient getInstance() {
        return new RetrofitClient();
    }

    /**
     * 通用
     *
     * @param tClass retrofit
     * @return service
     */
    public <T> T createService(Class<T> tClass) {
        return builder
                .client(HttpClientHelper.getHttpClient())
                .build()
                .create(tClass);
    }

    /**
     * 下载
     *
     * @param tClass   retrofit
     * @param listener 监听
     * @return service
     */
    public <T> T createDownloadRangeService(Class<T> tClass, DownloadProgressListener listener) {
        return builder
                .client(HttpClientHelper.getDownloadHttpClient(listener))
                .build()
                .create(tClass);
    }

    /**
     * 上传
     *
     * @param tClass   retrofit
     * @param listener 监听
     * @return service
     */
    public <T> T createUploadService(Class<T> tClass, UploadProgressListener listener) {
        return builder
                .client(HttpClientHelper.getUploadHttpClient(listener))
                .build()
                .create(tClass);
    }

}
