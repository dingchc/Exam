package com.cmcc.library.wrapper.retrofit.core;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import io.reactivex.Observable;

/**
 * Created by liupeng_a on 2017/1/10.
 */

public interface HttpService {

    @GET
    Observable<ResponseBody> doGet(@Url String url);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> doPost(@Url String url, @FieldMap Map<String, String> paramMap);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFileWithRange(@Url String url, @Header("Range") String range);

    @Multipart
    @POST
    Observable<ResponseBody> uploadFiles(@Url String url, @PartMap Map<String, RequestBody> paramMap, @Part MultipartBody.Part... file);
}
