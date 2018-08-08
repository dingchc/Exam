package com.cmcc.library.wrapper.retrofit.core;

import com.cmcc.library.wrapper.retrofit.CMStaticWrapper;
import com.cmcc.library.wrapper.retrofit.util.CMAppLogger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Https访问
 *
 * @author ding
 *         Created by ding on 30/01/2018.
 */

public class CMHttpsClientHelper {

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

        if (CMAppLogger.BuildConfig.DEBUG) {

            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (!builder.interceptors().contains(logger)) {
                builder.addInterceptor(logger);
            }
        }

        builder.sslSocketFactory(CMHttpsFactory.getSSLSocketFactory(CMStaticWrapper.getAppContext(), CMHttpsFactory.certificates));
        builder.hostnameVerifier(new CMHttpsFactory.TrustHostnameVerifier());

        return builder.build();
    }
}
