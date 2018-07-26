package com.aspirecn.exam.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.aspirecn.exam.AppLogger;
import com.aspirecn.exam.R;

import java.util.List;

/**
 * Created by ding on 05/02/2018.
 */

public class LoginActivity extends BaseMvpActivity<LoginPresenter, LoginView> implements LoginView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.executor_layout);

        Button btnClick1 = (Button) findViewById(R.id.btn_click1);
        Button btnClick2 = (Button) findViewById(R.id.btn_click2);

        btnClick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppLogger.i("btnClick1 click");
                presenter.loadData1();
            }
        });

    }

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void onLoadFinish1(List<String> dataList) {

        AppLogger.i("dataList.size()=" + dataList.size());
    }

    @Override
    public void onLoadFinish2() {

    }
}
