package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.listener.CMUploadProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上传拦截器
 * @author Ding
 */

public class CMUploadInterceptor implements Interceptor {

    private CMUploadProgressListener listener;

    CMUploadInterceptor(CMUploadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request request = originalRequest.newBuilder()
                .method(originalRequest.method(), new CMUploadRequestBody(originalRequest.body(), listener))
                .build();

        return chain.proceed(request);
    }
}
