package com.cmcc.exam.eduand;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmcc.exam.AppLogger;
import com.cmcc.exam.R;
import com.cmcc.exam.utils.ACache;
import com.cmcc.exam.utils.AESUtils;
import com.cmcc.exam.utils.ParamsUtil;
import com.cmcc.library.wrapper.retrofit.CMHttpController;
import com.cmcc.library.wrapper.retrofit.listener.CMHttpCallback;
import com.cmcc.library.wrapper.retrofit.listener.CMHttpProgressCallback;
import com.cmcc.library.wrapper.retrofit.model.CMBaseResponse;
import com.cmcc.library.wrapper.retrofit.model.CMUploadFileInfo;
import com.cmcc.library.wrapper.retrofit.util.CMAppLogger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 登录验证
 *
 * @author Ding
 *         Created by Ding on 2018/7/10.
 */

public class LoginApiActivity extends AppCompatActivity {

    /**
     * 正式环境
     */
    private static final String INTERNET_SERVER_9999 = "https://edu.10086.cn/";

    /**
     * 获取图形验证码接口
     */
    public static String SERVER_GET_KAPTCHA = "eduapi/kaptcha.jpg?update=";

    /**
     * 正式环境
     */
    public static final String LOGIN_AUTHENTICATION_9999 = "https://edu.10086.cn/sso/";

    /**
     * 票据
     */
    public static final String REST = "rest/tickets";

    /**
     * 2,3 验证接口,获取用户信息接口
     */
    public static final String TICKET = "eduapi/login/user/login";


    private static final String TYPE = "APP";

    /**
     * 校验图形验证码接口
     */
    public static String SERVER_LOGIN_KAPTCHA = "eduapi/login/kaptcha";


    /**
     * 访问微服务公共接口
     */
    public static final String SERVER_PUBLIC = "eduapi/micro/getDatas";


    public static final String SERVER_PUBLIC2 = "eduapi/micro/getDatas2";


    /**
     * 终端制造商
     */
    private static final String MANUFACTURER = android.os.Build.MANUFACTURER;

    /**
     * 终端系统
     */
    private static final String RELEASE = android.os.Build.VERSION.RELEASE;


    private static final int CODE_REST = 201;

    private TextInputEditText mVerifyCodeEditText;

    private ImageView mVerifyCodeImageView;

    private String mCookie;

    private String mCodeSign;

    private ACache mACache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edu_api_login);

        mVerifyCodeImageView = findViewById(R.id.iv_verify_code);
        mVerifyCodeEditText = findViewById(R.id.et_verify_code);

        mACache = ACache.get(this);

        String cacheString = mACache.getAsString("dcc");
        CMAppLogger.i("cacheString=" + cacheString);

        String picturePath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + "1.jpg";

        File file = new File(picturePath);

        CMAppLogger.i("file=" + file.length());
    }

    /**
     * 获取验证码
     *
     * @param view 视图
     */
    public void getVerifyCode1(View view) {

        int seed = new Random().nextInt();

        String url = INTERNET_SERVER_9999 + SERVER_GET_KAPTCHA + seed;

//        String url = "https://edu.10086.cn/eduapi/kaptcha.jpg?update=1";

        CMHttpController.INSTANCE.doGet(url, new TreeMap<String, String>(), new CMHttpCallback() {
            @Override
            public void onSuccess(CMBaseResponse response, String json) {

                try {
                    byte[] data = json.getBytes("utf-8");

                    AppLogger.i("data.length=" + data.length);

                    CMHttpController.printByteArray(data);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    if (bitmap != null) {
                        AppLogger.i("bitmap.getWidth()=" + bitmap.getWidth() + ", " + bitmap.getHeight());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Throwable e) {

                e.printStackTrace();
            }
        });
    }

    /**
     * 获取验证码
     *
     * @param view 视图
     */
    public void getVerifyCode(View view) {

        CMAppLogger.i("getVerifyCode");

        int seed = new Random().nextInt();

        String url = INTERNET_SERVER_9999 + SERVER_GET_KAPTCHA + seed;

        Observable.just(url)
                .subscribeOn(Schedulers.newThread())
                .map(new Function<String, byte[]>() {
                    @Override
                    public byte[] apply(String url) throws Exception {

                        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

                        Request request = new Request.Builder().url(url).get().build();

                        Call call = okHttpClient.newCall(request);

                        Response response = call.execute();

                        // 解析Cookie
                        StringBuilder sb = new StringBuilder();
                        for (Cookie cookie : Cookie.parseAll(response.request().url(), response.headers())) {
                            sb.append(cookie.name()).append("=").append(cookie.value()).append(";");
                        }

                        mCookie = sb.toString();

                        CMAppLogger.i("cookie=" + mCookie);

                        // 返回数组
                        byte[] data = null;

                        ResponseBody body = response.body();
                        if (body != null) {
                            data = body.bytes();
                        }

                        return data;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResourceObserver<byte[]>() {

                    @Override
                    public void onNext(byte[] data) {

                        if (data != null) {

                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                            if (bitmap != null) {
                                mVerifyCodeImageView.setImageBitmap(bitmap);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 登录
     *
     * @param view 控件
     */
    public void login(View view) {

        final long begin = System.currentTimeMillis();

        CMAppLogger.i("login");

        final String verifyCode = mVerifyCodeEditText.getText().toString();

        if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        Observable
                .just("")
                .subscribeOn(Schedulers.newThread())
                // 验证验证码
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {

                        String result = getResultForCheckVerifyCode(verifyCode);

                        if (TextUtils.isEmpty(result)) {
                            throw new IllegalArgumentException("服务器返回失败");
                        }

                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(result).getAsJsonObject();

                        // 验证成功
                        if (jsonObject.get("ret").getAsInt() == 0) {
                            mCodeSign = jsonObject.get("body").getAsString();
                        }
                        // 验证失败
                        else {
                            String errorMsg = jsonObject.get("msg").getAsString();
                            throw new IllegalArgumentException(errorMsg);
                        }

                        CMAppLogger.i("result=" + result);
                        return 0;
                    }
                })
                // 获取Location
                .map(new Function<Integer, Response>() {

                    @Override
                    public Response apply(Integer code) throws Exception {

                        return getLocation(LOGIN_AUTHENTICATION_9999 + REST);
                    }
                })
                // 获取ticket
                .map(new Function<Response, String>() {
                    @Override
                    public String apply(Response response) throws Exception {

                        CMAppLogger.i("code=" + response.code() + ", location=" + response.header("location"));

                        String ticket = "";

                        // 判断状态码
                        if (CODE_REST == response.code()) {

                            Response ticketRes = getTicket(response.header("location"));

                            if (ticketRes != null) {

                                ResponseBody body = ticketRes.body();
                                if (body != null) {

                                    ticket = body.string();
                                }
                            }
                        }

                        return ticket;
                    }
                })
                // 获取用户信息
                .map(new Function<String, String>() {

                    @Override
                    public String apply(String ticket) throws Exception {

                        CMAppLogger.i("ticket=" + ticket);

                        Response response = getUserInfo(ticket);

                        ResponseBody responseBody = response.body();

                        String json = "";

                        if (responseBody != null) {

                            json = responseBody.string();
                            CMAppLogger.i("info=" + json);

                            mACache.put("dcc", json);

                            String cacheString = mACache.getAsString("dcc");
                            CMAppLogger.i("cacheString=" + cacheString);
                        }

                        return json;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResourceObserver<String>() {
                    @Override
                    public void onNext(String string) {

                    }

                    @Override
                    public void onError(Throwable e) {

                        e.printStackTrace();

                        Toast.makeText(LoginApiActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                        CMAppLogger.i("duration=" + ((System.currentTimeMillis() - begin) / 1000));
                    }
                });
    }

    /**
     * 验证验证码
     *
     * @param code 验证码
     * @return 验证结果
     * @throws IOException 异常
     */
    private String getResultForCheckVerifyCode(String code) throws IOException {

        String url = INTERNET_SERVER_9999 + SERVER_LOGIN_KAPTCHA;

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        FormBody body = new FormBody.Builder().add("verificationCode", code).build();

        Request request = new Request.Builder().addHeader("Cookie", mCookie).url(url).post(body).build();

        Call call = okHttpClient.newCall(request);

        Response response = call.execute();

        ResponseBody responseBody = response.body();

        if (responseBody != null) {
            return responseBody.string();
        }

        return "";
    }

    /**
     * 获取ticket所需的地址
     *
     * @param url 请求地址
     * @return 响应
     * @throws IOException 异常
     */
    private Response getLocation(String url) throws IOException {

        CMAppLogger.i("thread=" + Thread.currentThread().getName());

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        FormBody body = new FormBody.Builder()
                .add("username", "13788878990")
                .add("password", "xxt878990")
                .add("termType", TYPE)
                .add("termManufacturer", MANUFACTURER)
                .add("termSystem", "android" + RELEASE)
                .add("province", "1262")
                .add("role", "1")
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = okHttpClient.newCall(request);

        return call.execute();
    }

    /**
     * 获取票据
     *
     * @param url 请求地址
     * @return 响应
     * @throws IOException 异常
     */
    private Response getTicket(String url) throws IOException {

        CMAppLogger.i("thread=" + Thread.currentThread().getName());

        String service = INTERNET_SERVER_9999 + TICKET;

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        FormBody body = new FormBody.Builder()
                .add("service", service)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = okHttpClient.newCall(request);

        return call.execute();

    }

    /**
     * 获取用户信息
     *
     * @param ticket 票据
     * @return 响应
     * @throws IOException 异常
     */
    private Response getUserInfo(String ticket) throws IOException {

        CMAppLogger.i("thread=" + Thread.currentThread().getName());

        String url = INTERNET_SERVER_9999 + TICKET;

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        FormBody body = new FormBody.Builder()
                .add("ticket", ticket)
                .add("verificationSign", mCodeSign)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = okHttpClient.newCall(request);

        return call.execute();

    }

    /**
     * 获取班级
     */
    public void getClasses(View view) {

        String url = INTERNET_SERVER_9999 + SERVER_PUBLIC;

        TreeMap<String, String> paramMap = new TreeMap<>();

        paramMap.put("userId", "100357953797");
        paramMap.put("url", "user_url");
        paramMap.put("method", "/org/getClassUserInfo");
        paramMap.put("type", "post");
        paramMap.put("condition", ParamsUtil.getParams(paramMap));

        CMHttpController.INSTANCE.doPost(url, paramMap, new CMHttpCallback() {
            @Override
            public void onSuccess(CMBaseResponse response, String json) {

                try {
                    CMAppLogger.i("json=" + AESUtils.decrypt(json.replaceAll("\"", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Throwable e) {

                e.printStackTrace();
            }
        });

    }

    /**
     * 上传文件
     *
     * @param view 控件
     */
    public void uploadFile(View view) {

        String url = INTERNET_SERVER_9999 + SERVER_PUBLIC2;

        String picturePath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + "2.jpg";

        // condition的映射
        Map<String, String> conditionMap = new HashMap<>();

        conditionMap.put("userId", "100357953797");
        conditionMap.put("classId", "339178392810");
        conditionMap.put("file", picturePath);


        // Multi Part 参数
        TreeMap<String, String> partMap = new TreeMap<>();

        partMap.put("url", "user_url");
        partMap.put("method", "/org/uploadimage");
        partMap.put("type", "post");
        partMap.put("condition", ParamsUtil.getParams(conditionMap));

        partMap.put("objectType", "2");


        List<CMUploadFileInfo> fileList = new ArrayList<>();
        fileList.add(new CMUploadFileInfo(picturePath, "file"));

        CMHttpController.INSTANCE.doUpload(url, partMap, null, fileList, new CMHttpProgressCallback() {
            @Override
            public void progress(long current, long total, boolean done) {
                CMAppLogger.i("current=" + current + ", total=" + total);
            }

            @Override
            public void onSuccess(CMBaseResponse response, String json) {

                try {
                    CMAppLogger.i("json=" + AESUtils.decrypt(json.replaceAll("\"", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Throwable e) {

                e.printStackTrace();
            }
        });
    }


}
