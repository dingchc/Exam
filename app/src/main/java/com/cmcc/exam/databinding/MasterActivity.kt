package com.cmcc.exam.databinding

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.cmcc.exam.R

/**
 *
 * @author ding
 * Created by Ding on 2018/8/7.
 */
class MasterActivity : AppCompatActivity() {

    var mDataRecyclerView: RecyclerView? = null
    var mDataAdapter: MasterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_master)

        mDataRecyclerView = findViewById(R.id.rv_data)

        val list = mutableListOf<MasterInfo>()

        for (i in 0..2) {
            list.add(MasterInfo("name " + i, i))
        }

        mDataAdapter = MasterAdapter(this, list)

        mDataRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mDataRecyclerView?.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        mDataRecyclerView?.adapter = mDataAdapter

        val handler = Handler()

        handler.postDelayed({
            mDataAdapter?.getDataList()?.add(MasterInfo("dcc", 5))
            mDataAdapter?.notifyItemInserted(mDataAdapter?.getDataList()!!.size - 1)
        }, 5000L)
    }

}