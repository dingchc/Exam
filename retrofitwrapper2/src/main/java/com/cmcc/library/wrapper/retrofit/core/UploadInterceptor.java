package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.UploadRequestBody;
import com.cmcc.library.wrapper.retrofit.listener.UploadProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liupeng_a on 2017/1/10.
 */

public class UploadInterceptor implements Interceptor {

    private UploadProgressListener listener;

    public UploadInterceptor(UploadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request request = originalRequest.newBuilder()
                .method(originalRequest.method(), new UploadRequestBody(originalRequest.body(), listener))
                .build();

        return chain.proceed(request);
    }
}
