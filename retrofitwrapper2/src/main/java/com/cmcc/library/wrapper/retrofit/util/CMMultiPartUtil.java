package com.cmcc.library.wrapper.retrofit.util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * MultiPart工具类
 *
 * @author Ding
 *         Created by ding on 12/26/16.
 */

public class CMMultiPartUtil {

    public final static String MULTIPART_FORM_DATA = "multipart/form-data";

    public static RequestBody createPartFromString(String value) {

        return RequestBody.create(MultipartBody.FORM, value);

    }
}
