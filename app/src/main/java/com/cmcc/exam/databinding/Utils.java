package com.cmcc.exam.databinding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * @author ding
 * Created by ding on 11/21/17.
 */

public class Utils {


    @BindingAdapter("bind:data")
    public static void setData(RecyclerView recyclerView, List<String> dataList) {

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//        recyclerView.setAdapter(new MembersAdapter(recyclerView.getContext(), dataList));
    }
}
