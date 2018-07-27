package com.cmcc.exam.databinding;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cmcc.exam.MyUser;
import com.cmcc.exam.R;

/**
 * Created by ding on 11/21/17.
 */

public class LoginActivity extends AppCompatActivity {

    private MyUser mMyUser;

    private int mIndex = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        mMyUser = new MyUser("dingchc", "工程师", "一年级一班");

        binding.setUser(mMyUser);

        handler.sendEmptyMessageDelayed(1, 2000);

        binding.btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, MemberActivity.class));
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            mIndex++;
            mMyUser.setTitle(mMyUser.getTitle() + mIndex);
            handler.sendEmptyMessageDelayed(1, 2000);
        }
    };
}
