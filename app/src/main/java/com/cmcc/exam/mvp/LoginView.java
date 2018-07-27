package com.cmcc.exam.mvp;

import java.util.List;

/**
 * Created by ding on 05/02/2018.
 */

public interface LoginView extends BaseView {

    /**
     * 加载1
     * @param dataList  数据
     */
    void onLoadFinish1(List<String> dataList);

    /**
     * 加载2
     */
    void onLoadFinish2();
}
