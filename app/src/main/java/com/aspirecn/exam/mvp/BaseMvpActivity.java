package com.aspirecn.exam.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author ding
 * Created by ding on 05/02/2018.
 */
public abstract class BaseMvpActivity<P extends BasePresenter, V extends BaseView> extends AppCompatActivity implements BaseView {

    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (presenter == null) {
            presenter = createPresenter();
        }

        presenter.attach((V) this);

    }

    /**
     * 创建Presenter
     * @return presenter
     */
    protected abstract P createPresenter();


    @Override
    protected void onDestroy() {

        if (presenter != null) {
            presenter.cancelRequest();
            presenter.detach();
        }

        super.onDestroy();
    }
}
