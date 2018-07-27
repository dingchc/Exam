package com.cmcc.library.wrapper.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.cmcc.library.wrapper.retrofit.core.CMHttpException;
import com.cmcc.library.wrapper.retrofit.core.DownloadRangeImpl;
import com.cmcc.library.wrapper.retrofit.core.HttpService;
import com.cmcc.library.wrapper.retrofit.core.RetrofitClient;
import com.cmcc.library.wrapper.retrofit.listener.HttpCallback;
import com.cmcc.library.wrapper.retrofit.listener.HttpProgressCallback;
import com.cmcc.library.wrapper.retrofit.listener.UploadProgressListener;
import com.cmcc.library.wrapper.retrofit.model.CMHttpTracker;
import com.cmcc.library.wrapper.retrofit.model.CMResponse;
import com.cmcc.library.wrapper.retrofit.model.CMBaseResponse;
import com.cmcc.library.wrapper.retrofit.model.CMUploadFileInfo;
import com.cmcc.library.wrapper.retrofit.util.CMAppLogger;
import com.cmcc.library.wrapper.retrofit.util.CMMultiPartUtil;
import com.cmcc.library.wrapper.retrofit.util.CMUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.functions.Function;
import io.reactivex.observers.ResourceObserver;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 请求核心类
 * 请先调用下setAppContext方法，下载情况需要读取默认存储
 *
 * @author Ding
 *         Created by Ding on 2/28/17.
 */

public enum HttpController {

    /**
     *
     */
    INSTANCE;

    private final String EMPTY_STR = "";
    private ConcurrentHashMap<CMHttpTracker, WeakReference<ResourceObserver>> requestMap;

    HttpController() {
        requestMap = new ConcurrentHashMap();
    }

    /**
     * 设置全局App上下文
     *
     * @param context 上下文
     */
    public void setAppContext(Context context) {
        CMStaticWrapper.setAppContext(context);
    }


    /**
     * 加载url
     *
     * @param url      地址
     * @param params   参数
     * @param callback 回调
     */
    public CMHttpTracker doGet(final String url, Map<String, String> params, final HttpCallback callback) {

        final CMHttpTracker tracker = new CMHttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                CMAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }
                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    CMAppLogger.i("thread=" + Thread.currentThread().getName());
                    byte[] data = responseBody.bytes();

                    printByteArray(data);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    CMAppLogger.i("bitmap.getWidth()=" + bitmap.getWidth() + ", " + bitmap.getHeight() + ", " + responseBody);

//                    String json = responseBody.string();
                    String json = new String(data, "utf-8");

//                    MSAppLogger.i("json=" + json);

                    if (callback != null) {
                        callback.onSuccess(null, json);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onException(e);
                    }
                    e.printStackTrace();
                }

            }
        };

        requestMap.put(tracker, new WeakReference(subscriber));
        RetrofitClient.getInstance().createService(HttpService.class).doGet(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

        return tracker;
    }

    /**
     * 打印Byte数组
     *
     * @param buffer Byte数组
     * @return 字符串
     */
    public static String printByteArray(byte[] buffer) {

        if (buffer != null && buffer.length > 0) {

            StringBuffer sb = new StringBuffer();

            sb.append("length=" + buffer.length + " ");

            sb.append("[");
            for (byte value : buffer) {
                sb.append(value).append(", ");
            }

            sb.delete(sb.length() - 2, sb.length());

            sb.append("]");

            return sb.toString();
        } else {
            return "[]";
        }
    }

    /**
     * 加载url
     *
     * @param url      地址
     * @param params   参数
     * @param callback 回调
     */
    public CMHttpTracker doPost(final String url, Map<String, String> params, final HttpCallback callback) {

        final CMHttpTracker tracker = new CMHttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                CMAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }
                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    String json = responseBody.string();

                    CMAppLogger.i("json=" + json);

                    if (callback != null) {
                        callback.onSuccess(null, json);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onException(e);
                    }
                    e.printStackTrace();
                }

            }
        };

        requestMap.put(tracker, new WeakReference(subscriber));
        RetrofitClient.getInstance().createService(HttpService.class).doPost(url, params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

        return tracker;
    }

    /**
     * 加载url
     *
     * @param url      地址
     * @param params   参数
     * @param clazz    转Json的类
     * @param callback 回调
     */
    public CMHttpTracker doPost(final String url, Map<String, String> params, final Class<? extends CMBaseResponse> clazz, final HttpCallback callback) {

        final CMHttpTracker tracker = new CMHttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                CMAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }
                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    Gson gson = new Gson();

                    String json = responseBody.string();

                    CMAppLogger.i("json=" + json);

                    CMBaseResponse t = gson.fromJson(json, clazz);

                    if (callback != null) {
                        callback.onSuccess(t, json);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onException(e);
                    }
                    e.printStackTrace();
                }

            }
        };

        requestMap.put(tracker, new WeakReference(subscriber));
        RetrofitClient.getInstance().createService(HttpService.class).doPost(url, params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

        return tracker;
    }

    /**
     * 加载url
     *
     * @param url      地址
     * @param params   参数
     * @param type     转Json的TypeToken
     * @param callback 回调
     */
    public CMHttpTracker doPost(final String url, Map<String, String> params, final Type type, final HttpCallback callback) {

        final CMHttpTracker tracker = new CMHttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                CMAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }

                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    Gson gson = new Gson();

                    String json = responseBody.string();

                    CMAppLogger.i("json=" + json);

                    CMResponse t = gson.fromJson(json, type);

                    if (callback != null) {
                        callback.onSuccess(t, json);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onException(e);
                    }
                    e.printStackTrace();
                }

            }
        };

        requestMap.put(tracker, new WeakReference(subscriber));
        RetrofitClient.getInstance().createService(HttpService.class).doPost(url, params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

        return tracker;
    }

    /**
     * 断点下载
     *
     * @param url      下载地址
     * @param callback 回调
     * @return 跟踪
     */
    public CMHttpTracker downloadFileByRange(final String url, final HttpProgressCallback callback) {

        // 检查全局上下文是否已设置
        if (CMUtil.checkObjNotNull(callback) && !CMUtil.checkObjNotNull(CMStaticWrapper.getAppContext())) {
            callback.onException(new Exception("must call method setAppContext in MSStaticWrapper.java"));
            return null;
        }

        // 如果是本地文件，直接返回
        if (!CMUtil.isWebUrl(url) && CMUtil.checkObjNotNull(callback)) {

            CMResponse<String> esResponse = new CMResponse<>();
            esResponse.data = url;
            callback.onSuccess(esResponse, null);
            return null;
        }

        final CMHttpTracker tracker = new CMHttpTracker(url);

        final ResourceObserver subscriber = new ResourceObserver<String>() {
            @Override
            public void onComplete() {
                CMAppLogger.i("onComplete");
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                CMAppLogger.e("onError" + e.getMessage());

                if (callback != null) {
                    callback.onException(e);
                }

                requestMap.remove(tracker);
            }

            @Override
            public void onNext(String path) {

                CMAppLogger.i("onNext " + path);

                CMResponse<String> esResponse = new CMResponse<>();
                esResponse.data = path;

                if (!TextUtils.isEmpty(path)) {
                    if (callback != null) {
                        callback.onSuccess(esResponse, null);
                    }
                } else {
                    if (callback != null) {
                        callback.onException(new CMHttpException("下载中断", CMHttpException.CODE_REQUEST_INTERCEPTED));
                    }
                }
            }
        };

        final DownloadRangeImpl downloadImpl = new DownloadRangeImpl(url, callback);

        // 文件已存在，直接返回
        if (CMUtil.checkFileExist(downloadImpl.getDestPath())) {

            CMResponse<String> esResponse = new CMResponse<>();
            esResponse.data = downloadImpl.getDestPath();

            if (callback != null) {
                callback.onSuccess(esResponse, null);
            }
            return tracker;
        }

        requestMap.put(tracker, new WeakReference(subscriber));

        RetrofitClient.getInstance().createDownloadRangeService(HttpService.class, downloadImpl).downloadFile(url)
                .map(new Function<ResponseBody, String>() {

                    @Override
                    public String apply(ResponseBody response) {


                        String destPath = null;

                        String tempPath = downloadImpl.getTempPath();

                        try {
                            boolean isSuccess = writeFile(response, tempPath, downloadImpl.isSupportRange(), downloadImpl);

                            CMAppLogger.i("isSuccess=" + isSuccess);
                            if (!isSuccess) {
                                CMAppLogger.i("return null");
                                return EMPTY_STR;
                            }
                        } catch (Exception e) {
                            return EMPTY_STR;
                        }

                        destPath = downloadImpl.getDestPath();
                        CMUtil.moveFile(tempPath, destPath);

                        return destPath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return tracker;
    }

    /**
     * 上传文件
     *
     * @param url                地址
     * @param paramMap           参数
     * @param type               json类型
     * @param uploadFileInfoList 文件list
     * @param callback           回调
     */
    public CMHttpTracker doUpload(final String url, TreeMap<String, String> paramMap, final Type type, final List<CMUploadFileInfo> uploadFileInfoList, final HttpProgressCallback callback) {

        if (uploadFileInfoList == null || uploadFileInfoList.size() <= 0) {

            if (CMUtil.checkObjNotNull(callback)) {
                callback.onException(new CMHttpException("no input file", CMHttpException.CODE_DEFAULT));
            }

            return null;
        }

        final CMHttpTracker tracker = new CMHttpTracker(url);

        // 上传的回调
        UploadProgressListener progressListener = new UploadProgressListener() {
            @Override
            public void progress(long current, long total, boolean done) {

                if (callback != null) {

                    callback.progress(current, total, done);
                }
            }
        };

        // 创建调用
        HttpService api = RetrofitClient.getInstance().createUploadService(HttpService.class, progressListener);

        CMUploadFileInfo[] fileInfoArray = new CMUploadFileInfo[uploadFileInfoList.size()];

        uploadFileInfoList.toArray(fileInfoArray);

        MultipartBody.Part[] partArray = createMultipartBodyPartArray(fileInfoArray);

        TreeMap<String, RequestBody> bodyTreeMap = transformParamsMap(paramMap);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
                CMAppLogger.i("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

                CMAppLogger.i("onError");

                if (callback != null) {
                    callback.onException(e);
                }

                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                try {

                    Gson gson = new Gson();

                    String json = responseBody.string();

                    CMAppLogger.i("onNext json = " + json);

                    CMResponse t = null;

                    if (CMUtil.checkObjNotNull(type)) {
                        t = gson.fromJson(json, type);
                    }

                    if (callback != null) {
                        callback.onSuccess(t, json);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onException(e);
                    }

                    e.printStackTrace();
                }

            }
        };

        requestMap.put(tracker, new WeakReference(subscriber));

        api.uploadFiles(url, bodyTreeMap, partArray).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return tracker;
    }


    /**
     * 创建MultipartBody数组
     *
     * @param fileInfos 文件
     * @return Part数组
     */
    private MultipartBody.Part[] createMultipartBodyPartArray(CMUploadFileInfo... fileInfos) {

        MultipartBody.Part[] parts = null;

        if (fileInfos != null && fileInfos.length > 0) {

            parts = new MultipartBody.Part[fileInfos.length];

            int i = 0;

            for (CMUploadFileInfo fileInfo : fileInfos) {

                String fileName = CMUtil.getFileName(fileInfo.filePath);

                if (!TextUtils.isEmpty(fileName)) {
                    RequestBody requestBody = RequestBody.create(MultipartBody.FORM, new File(fileInfo.filePath));
                    parts[i] = MultipartBody.Part.createFormData(fileInfo.fileKey, fileName, requestBody);
                    i++;
                }
            }

        }

        return parts;
    }

    /**
     * 参数转为FormData
     *
     * @param map 参数
     * @return body map
     */
    private TreeMap<String, RequestBody> transformParamsMap(TreeMap<String, String> map) {

        TreeMap<String, RequestBody> bodyTreeMap = new TreeMap<>();

        if (map != null) {
            for (String key : map.keySet()) {

                bodyTreeMap.put(key, CMMultiPartUtil.createPartFromString(map.get(key)));
            }
        }

        return bodyTreeMap;
    }

    /**
     * 取消请求
     *
     * @param tracker 请求地址
     */
    public void cancelRequest(CMHttpTracker tracker) {

        if (CMUtil.checkObjNotNull(tracker)) {

            if (requestMap.containsKey(tracker)) {
                WeakReference<ResourceObserver> weakReference = requestMap.get(tracker);
                ResourceObserver subscriber = weakReference.get();

                if (CMUtil.checkObjNotNull(subscriber)) {
                    subscriber.onError(new CMHttpException("cancel request", CMHttpException.CODE_REQUEST_CANCELED));
                    subscriber.dispose();
                }
            }

        }
    }

    /**
     * 检查是否正在进行（主要用于判断下载）
     *
     * @param url 请求地址
     * @return true 正在运行、false 未运行
     */
    public boolean checkIsDoing(String url) {

        boolean ret = false;

        for (CMHttpTracker httpTracker : requestMap.keySet()) {
            if (CMUtil.checkObjNotNull(httpTracker) && !TextUtils.isEmpty(httpTracker.getUrl()) && httpTracker.getUrl().equals(url)) {
                ret = true;
                break;
            }
        }

        CMAppLogger.i("ret=" + ret);

        return ret;
    }

    /**
     * 保存文件
     *
     * @param responseBody      响应
     * @param path              输出文件路径
     * @param isSupportRange    是否支持断点
     * @param downloadRangeImpl 回调
     * @return true 保存成功、false 保存失败
     */
    public static boolean writeFile(ResponseBody responseBody, String path, boolean isSupportRange, DownloadRangeImpl downloadRangeImpl) throws Exception {

        boolean ret = false;

        File saveFile = new File(path);

        OutputStream outputStream = null;

        InputStream inputStream = null;
        try {
            byte[] buffer = new byte[4096];

            long downloadSize = saveFile.length();

            long availableSize = responseBody.contentLength();

            long total = downloadSize + availableSize;

            long current = 0 + downloadSize;

            inputStream = responseBody.byteStream();

            CMAppLogger.i("downloadSize=" + downloadSize + ",availableSize=" + availableSize + ",total=" + total + ", current=" + current);

            outputStream = new FileOutputStream(saveFile, isSupportRange);

            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);

                current += read;

                if (CMUtil.checkObjNotNull(downloadRangeImpl)) {
                    downloadRangeImpl.sendProgressMessage(current, total);
                }
            }

            outputStream.flush();

            ret = true;
        } catch (Exception e) {
//            e.printStackTrace();
            CMAppLogger.e(e.getMessage());
//            throw new MSHttpException("download intercept", MSHttpException.CODE_REQUEST_INTERCEPTED);
            ret = false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * 获取临时文件路径
     *
     * @param url 文件下载地址
     * @return 临时文件路径
     */
    public String getDownloadTempPath(String url) {

        return DownloadRangeImpl.createTempPath(url);
    }

    /**
     * 获取目标文件路径
     *
     * @param url 文件下载地址
     * @return 目标文件路径
     */
    public String getDownloadDestPath(String url) {

        return DownloadRangeImpl.createDestPath(url);
    }

}
