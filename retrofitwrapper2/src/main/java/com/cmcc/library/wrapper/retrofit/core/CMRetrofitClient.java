package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.listener.CMDownloadProgressListener;
import com.cmcc.library.wrapper.retrofit.listener.CMUploadProgressListener;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit封装类
 *
 * @author Ding
 *         Created by ding on 12/23/16.
 */

public class CMRetrofitClient {

    private Retrofit.Builder builder;

    private CMRetrofitClient() {
        builder = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public static CMRetrofitClient getInstance() {
        return new CMRetrofitClient();
    }

    /**
     * 通用
     *
     * @param tClass retrofit
     * @return service
     */
    public <T> T createService(Class<T> tClass) {
        return builder
                .client(CMHttpClientHelper.getHttpClient())
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
    public <T> T createDownloadRangeService(Class<T> tClass, CMDownloadProgressListener listener) {
        return builder
                .client(CMHttpClientHelper.getDownloadHttpClient(listener))
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
    public <T> T createUploadService(Class<T> tClass, CMUploadProgressListener listener) {
        return builder
                .client(CMHttpClientHelper.getUploadHttpClient(listener))
                .build()
                .create(tClass);
    }

}
