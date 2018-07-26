package com.aspirecn.library.wrapper.retrofit.core;



import com.aspirecn.library.wrapper.retrofit.core.HttpClientHelper;
import com.aspirecn.library.wrapper.retrofit.listener.DownloadProgressListener;
import com.aspirecn.library.wrapper.retrofit.listener.UploadProgressListener;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ding on 12/23/16.
 */

public class RetrofitClient {

    private Retrofit.Builder builder;

    private RetrofitClient(){
        builder = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public static RetrofitClient getInstance(){
        return new RetrofitClient();
    }

    /**
     * 通用
     * @param tClass retrofit
     * @return 返回
     */
    public <T> T createService(Class<T> tClass){
        return builder
                .client(HttpClientHelper.getHttpClient())
                .build()
                .create(tClass);
    }

    /**
     * 下载
     * @param tClass retrofit
     * @param listener 监听
     * @return
     */
    public <T> T createDownloadService(Class<T> tClass, DownloadProgressListener listener){
        return builder
                .client(HttpClientHelper.getDownloadHttpClient(listener, false))
                .build()
                .create(tClass);
    }

    /**
     * 下载
     * @param tClass retrofit
     * @param listener 监听
     * @return
     */
    public <T> T createDownloadRangeService(Class<T> tClass, DownloadProgressListener listener){
        return builder
                .client(HttpClientHelper.getDownloadHttpClient(listener, true))
                .build()
                .create(tClass);
    }

    /**
     * 上传
     * @param tClass retrofit
     * @param listener 监听
     * @return 返回
     */
    public <T> T createUploadService(Class<T> tClass, UploadProgressListener listener){
        return builder
                .client(HttpClientHelper.getUploadHttpClient(listener))
                .build()
                .create(tClass);
    }

    /**
     * 通用
     * @param tClass retrofit
     * @return 返回
     */
    public <T> T createHttpsService(Class<T> tClass){
        return builder
                .client(HttpsClientHelper.getHttpClient())
                .build()
                .create(tClass);
    }
}
