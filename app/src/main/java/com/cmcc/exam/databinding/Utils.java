package com.cmcc.exam.databinding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.List;

/**
 * @author ding
 * Created by ding on 11/21/17.
 */

public class Utils {


    @BindingAdapter("bind:data")
    public static void setData(RecyclerView recyclerView, List<String> dataList) {

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MembersAdapter(recyclerView.getContext(), dataList));
    }

    @BindingAdapter("bind:image")
    public static void setImage(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }
}
