package com.cmcc.exam.mvp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ding on 05/02/2018.
 */

public class LoginPresenter extends BasePresenter<LoginView> {

    public LoginPresenter() {
        super();
    }


    public void loadData1() {

        List<String> dataList = new ArrayList<String>();
        dataList.add("1");
        dataList.add("2");

        if (view != null) {
            view.onLoadFinish1(dataList);
        }
    }
}
