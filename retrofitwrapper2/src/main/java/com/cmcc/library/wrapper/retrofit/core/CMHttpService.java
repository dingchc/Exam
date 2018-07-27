package com.cmcc.library.wrapper.retrofit.core;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import io.reactivex.Observable;

/**
 * 请求定义类
 *
 * @author Ding
 */

public interface CMHttpService {

    /**
     * Get请求
     *
     * @param url 地址
     * @return 可观察对象
     */
    @GET
    Observable<ResponseBody> doGet(@Url String url);

    /**
     * Post请求
     *
     * @param url      地址
     * @param paramMap 参数Map
     * @return 可观察对象
     */
    @FormUrlEncoded
    @POST
    Observable<ResponseBody> doPost(@Url String url, @FieldMap Map<String, String> paramMap);

    /**
     * 下载文件，支持断点
     *
     * @param url 地址
     * @return 可观察对象
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    /**
     * 对文件上传
     *
     * @param url      地址
     * @param paramMap 参数Map
     * @param files    文件列表
     * @return 可观察对象
     */
    @Multipart
    @POST
    Observable<ResponseBody> uploadFiles(@Url String url, @PartMap Map<String, RequestBody> paramMap, @Part MultipartBody.Part... files);
}
