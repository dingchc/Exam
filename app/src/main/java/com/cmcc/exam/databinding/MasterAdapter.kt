package com.cmcc.exam.databinding

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cmcc.exam.R

/**
 * @author ding
 * Created by Ding on 2018/8/7.
 */
class MasterAdapter(context: Context?) : Adapter<MasterAdapter.MyViewHolder>() {

    private var mContext = context

    private var mDataList: MutableList<MasterInfo>? = null

    constructor(context: Context?, dataList: MutableList<MasterInfo>) : this(context) {

        mDataList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MasterAdapter.MyViewHolder {

        val binding: ItemMasterBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_master, parent, false)

        return MyViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: MasterAdapter.MyViewHolder?, position: Int) {

        val binding: ItemMasterBinding? = DataBindingUtil.getBinding<ItemMasterBinding>(holder!!.itemView)

        binding?.masterInfo = mDataList?.get(position)
        binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mDataList!!.size
    }

    /**
     * 返回数据列表
     */
    fun getDataList() : MutableList<MasterInfo>? {
        return mDataList
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}

