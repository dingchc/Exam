package com.aspirecn.library.wrapper.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.aspirecn.library.wrapper.retrofit.core.MSHttpException;
import com.aspirecn.library.wrapper.retrofit.core.DownloadRangeImpl;
import com.aspirecn.library.wrapper.retrofit.core.HttpService;
import com.aspirecn.library.wrapper.retrofit.core.RetrofitClient;
import com.aspirecn.library.wrapper.retrofit.listener.HttpCallback;
import com.aspirecn.library.wrapper.retrofit.listener.HttpProgressCallback;
import com.aspirecn.library.wrapper.retrofit.listener.UploadProgressListener;
import com.aspirecn.library.wrapper.retrofit.model.HttpTracker;
import com.aspirecn.library.wrapper.retrofit.model.MSResponse;
import com.aspirecn.library.wrapper.retrofit.model.MSBaseResponse;
import com.aspirecn.library.wrapper.retrofit.model.MSUploadFileInfo;
import com.aspirecn.library.wrapper.retrofit.util.MSAppLogger;
import com.aspirecn.library.wrapper.retrofit.util.MSDirUtil;
import com.aspirecn.library.wrapper.retrofit.util.MSMultiPartUtil;
import com.aspirecn.library.wrapper.retrofit.util.MSUtil;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 请求类
 * 请先调用下setAppContext方法，下载情况需要读取默认存储
 * Created by Ding on 2/28/17.
 */

public enum HttpController {

    /**
     *
     */
    INSTANCE;

    private final String EMPTY_STR = "";
    private ConcurrentHashMap<HttpTracker, WeakReference<ResourceObserver>> requestMap;

    HttpController() {
        requestMap = new ConcurrentHashMap();
    }

    /**
     * 设置全局App上下文
     *
     * @param context 上下文
     */
    public void setAppContext(Context context) {
        MSStaticWrapper.setAppContext(context);
    }


    /**
     * 加载url
     *
     * @param url      地址
     * @param params   参数
     * @param callback 回调
     */
    public HttpTracker doGet(final String url, Map<String, String> params, final HttpCallback callback) {

        final HttpTracker tracker = new HttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }
                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    MSAppLogger.i("thread=" + Thread.currentThread().getName());
                    byte[] data = responseBody.bytes();

                    printByteArray(data);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    MSAppLogger.i("bitmap.getWidth()=" + bitmap.getWidth() +", " + bitmap.getHeight() + ", " + responseBody);

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
    public HttpTracker doPost(final String url, Map<String, String> params, final HttpCallback callback) {

        final HttpTracker tracker = new HttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }
                requestMap.remove(tracker);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    String json = responseBody.string();

                    MSAppLogger.i("json=" + json);

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
    public HttpTracker doPost(final String url, Map<String, String> params, final Class<? extends MSBaseResponse> clazz, final HttpCallback callback) {

        final HttpTracker tracker = new HttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.e("onError");

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

                    MSAppLogger.i("json=" + json);

                    MSBaseResponse t = gson.fromJson(json, clazz);

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
    public HttpTracker doPost(final String url, Map<String, String> params, final Type type, final HttpCallback callback) {

        final HttpTracker tracker = new HttpTracker(url);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.e("onError");

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

                    MSAppLogger.i("json=" + json);

                    MSResponse t = gson.fromJson(json, type);

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
     * 下载文件(如果文件已存在，直接返回结果)
     *
     * @param url      下载地址
     * @param callback 回调
     */
    public HttpTracker downloadFile(final String url, final HttpCallback callback) {

        // 检查全局上下文是否已设置
        if (MSUtil.checkObjNotNull(callback) && !MSUtil.checkObjNotNull(MSStaticWrapper.getAppContext())) {
            callback.onException(new Exception("must call method setAppContext in MSStaticWrapper.java"));
            return null;
        }
        // 如果是本地文件，直接返回
        if (!MSUtil.isWebUrl(url) && MSUtil.checkObjNotNull(callback)) {

            MSResponse<String> esResponse = new MSResponse<>();
            esResponse.data = url;
            callback.onSuccess(esResponse, null);
            return null;
        }


        final HttpTracker tracker = new HttpTracker(url);

        final ResourceObserver subscriber = new ResourceObserver<String>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.e("onError");

                if (callback != null) {
                    callback.onException(e);
                }

                requestMap.remove(tracker);
            }

            @Override
            public void onNext(String path) {

                MSResponse<String> esResponse = new MSResponse<>();
                esResponse.data = path;

                if (!TextUtils.isEmpty(path)) {
                    if (callback != null) {
                        callback.onSuccess(esResponse, null);
                    }
                } else {
                    if (callback != null) {
                        callback.onException(new MSHttpException("下载中断", MSHttpException.CODE_REQUEST_INTERCEPTED));
                    }
                }

            }
        };

        requestMap.put(tracker, new WeakReference(subscriber));

        RetrofitClient.getInstance().createDownloadService(HttpService.class, null).downloadFile(url)
                .map(new Function<ResponseBody, String>() {

                    @Override
                    public String apply(ResponseBody response) {

                        String fileName = MSUtil.getFileName(url);
                        String path = MSDirUtil.getValidPath(MSDirUtil.getDownloadDir(), fileName);

                        try {
                            boolean isSuccess = writeFile(response, path, false, null);

                            if (!isSuccess) {

                                // 删除已下载的文件
                                MSUtil.deleteAFile(path);
                                return EMPTY_STR;
                            }

                        } catch (Exception e) {
//                            subscriber.onError(e);
//                            e.printStackTrace();
                            return EMPTY_STR;
                        }

                        return path;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return tracker;
    }

    /**
     * 断点下载
     *
     * @param url      下载地址
     * @param callback 回调
     * @return 跟踪
     */
    public HttpTracker downloadFileByRange(final String url, final HttpProgressCallback callback) {

        // 检查全局上下文是否已设置
        if (MSUtil.checkObjNotNull(callback) && !MSUtil.checkObjNotNull(MSStaticWrapper.getAppContext())) {
            callback.onException(new Exception("must call method setAppContext in MSStaticWrapper.java"));
            return null;
        }

        // 如果是本地文件，直接返回
        if (!MSUtil.isWebUrl(url) && MSUtil.checkObjNotNull(callback)) {

            MSResponse<String> esResponse = new MSResponse<>();
            esResponse.data = url;
            callback.onSuccess(esResponse, null);
            return null;
        }

        final HttpTracker tracker = new HttpTracker(url);

        final ResourceObserver subscriber = new ResourceObserver<String>() {
            @Override
            public void onComplete() {
                MSAppLogger.i("onComplete");
                // 删除请求的订阅者
                requestMap.remove(tracker);
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.e("onError" + e.getMessage());

                if (callback != null) {
                    callback.onException(e);
                }

                requestMap.remove(tracker);
            }

            @Override
            public void onNext(String path) {

                MSAppLogger.i("onNext " + path);

                MSResponse<String> esResponse = new MSResponse<>();
                esResponse.data = path;

                if (!TextUtils.isEmpty(path)) {
                    if (callback != null) {
                        callback.onSuccess(esResponse, null);
                    }
                } else {
                    if (callback != null) {
                        callback.onException(new MSHttpException("下载中断", MSHttpException.CODE_REQUEST_INTERCEPTED));
                    }
                }
            }
        };

        final DownloadRangeImpl downloadImpl = new DownloadRangeImpl(url, callback);

        // 文件已存在，直接返回
        if (MSUtil.checkFileExist(downloadImpl.getDestPath())) {

            MSResponse<String> esResponse = new MSResponse<>();
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

                            MSAppLogger.i("isSuccess="+isSuccess);
                            if (!isSuccess) {
                                MSAppLogger.i("return null");
                                return EMPTY_STR;
                            }
                        } catch (Exception e) {
//                            subscriber.onError(e);
                            return EMPTY_STR;
                        }

                        destPath = downloadImpl.getDestPath();
                        MSUtil.moveFile(tempPath, destPath);

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
    public HttpTracker doUpload(final String url, TreeMap<String, String> paramMap, final Type type, final List<MSUploadFileInfo> uploadFileInfoList, final HttpProgressCallback callback) {

        if (uploadFileInfoList == null || uploadFileInfoList.size() <= 0) {

            if (MSUtil.checkObjNotNull(callback)) {
                callback.onException(new MSHttpException("no input file", MSHttpException.CODE_DEFAULT));
            }

            return null;
        }

        final HttpTracker tracker = new HttpTracker(url);

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

        MSUploadFileInfo[] fileInfoArray = new MSUploadFileInfo[uploadFileInfoList.size()];

        uploadFileInfoList.toArray(fileInfoArray);

        MultipartBody.Part[] partArray = createMultipartBodyPartArray(fileInfoArray);

        TreeMap<String, RequestBody> bodyTreeMap = transformParamsMap(paramMap);

        ResourceObserver subscriber = new ResourceObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                // 删除请求的订阅者
                requestMap.remove(tracker);
                MSAppLogger.i("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

                MSAppLogger.i("onError");

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

                    MSAppLogger.i("onNext json = " + json);

                    MSResponse t = null;

                    if (MSUtil.checkObjNotNull(type)) {
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
    private MultipartBody.Part[] createMultipartBodyPartArray(MSUploadFileInfo... fileInfos) {

        MultipartBody.Part[] parts = null;

        if (fileInfos != null && fileInfos.length > 0) {

            parts = new MultipartBody.Part[fileInfos.length];

            int i = 0;

            for (MSUploadFileInfo fileInfo : fileInfos) {

                String fileName = MSUtil.getFileName(fileInfo.filePath);

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

                bodyTreeMap.put(key, MSMultiPartUtil.createPartFromString(map.get(key)));
            }
        }

        return bodyTreeMap;
    }

    /**
     * 取消请求
     *
     * @param tracker 请求地址
     */
    public void cancelRequest(HttpTracker tracker) {

        if (MSUtil.checkObjNotNull(tracker)) {

            if (requestMap.containsKey(tracker)) {
                WeakReference<ResourceObserver> weakReference = requestMap.get(tracker);
                ResourceObserver subscriber = weakReference.get();

                if (MSUtil.checkObjNotNull(subscriber)) {
                    subscriber.onError(new MSHttpException("cancel request", MSHttpException.CODE_REQUEST_CANCELED));
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

        for (HttpTracker httpTracker : requestMap.keySet()) {
            if (MSUtil.checkObjNotNull(httpTracker) && !TextUtils.isEmpty(httpTracker.getUrl()) && httpTracker.getUrl().equals(url)) {
                ret = true;
                break;
            }
        }

        MSAppLogger.i("ret="+ret);

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

            MSAppLogger.i("downloadSize=" + downloadSize + ",availableSize=" + availableSize + ",total=" + total + ", current=" + current);

            outputStream = new FileOutputStream(saveFile, isSupportRange);

            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);

                current += read;

                if (MSUtil.checkObjNotNull(downloadRangeImpl)) {
                    downloadRangeImpl.sendProgressMessage(current, total);
                }
            }

            outputStream.flush();

            ret = true;
        } catch (Exception e) {
//            e.printStackTrace();
            MSAppLogger.e(e.getMessage());
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
