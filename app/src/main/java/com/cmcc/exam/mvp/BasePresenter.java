package com.cmcc.exam.mvp;

/**
 * @author ding
 * Created by ding on 05/02/2018.
 */

public class BasePresenter<V extends BaseView> {

    protected V view;

    public BasePresenter() {
    }

    /**
     * 绑定
     * @param view 试图
     */
    public void attach(V view) {
        this.view = view;
    }

    /**
     * 解绑
     */
    public void detach() {
        this.view = null;
    }

    /**
     * 取消请求
     */
    public void cancelRequest() {
    }
}
