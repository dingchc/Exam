package com.aspirecn.exam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aspirecn.exam.widget.MsToolbar;

/**
 * @author ding
 * Created by ding on 2018/5/14.
 */

public class CoordinatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coordinator);

        MsToolbar toolbar = findViewById(R.id.ms_toolbar);

        setSupportActionBar(toolbar);
    }
}
