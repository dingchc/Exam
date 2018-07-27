package com.cmcc.library.wrapper.retrofit;

import android.content.Context;

/**
 * 上下文
 *
 * @author Ding
 *         Created by ding on 4/24/17.
 */

public class CMStaticWrapper {

    /**
     * 上下文
     */
    private static Context appContext;


    /**
     * 获取应用上下文
     *
     * @return 上下文
     */
    public static Context getAppContext() {
        return appContext;
    }

    /**
     * 设置上下文
     *
     * @param appContext 上下文
     */
    public static void setAppContext(Context appContext) {
        CMStaticWrapper.appContext = appContext;
    }
}
