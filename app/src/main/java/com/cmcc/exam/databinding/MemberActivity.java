package com.cmcc.exam.databinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.cmcc.exam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ding on 11/21/17.
 */

public class MemberActivity extends AppCompatActivity {

    private RecyclerView mRvData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMembersBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_members);

        List<String> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add("stu " + i);
        }
        binding.setMemberList(memberList);
    }
}
