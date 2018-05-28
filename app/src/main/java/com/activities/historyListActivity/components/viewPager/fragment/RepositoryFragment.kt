package com.activities.historyListActivity.components.viewPager.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.annotations.FuckingStaticSingleton
import com.example.starkre.sleepAlertHistory.R
import java.util.logging.Logger

@FuckingStaticSingleton
class RepositoryFragment : Fragment() {

    companion object {
        @JvmStatic
        var log = Logger.getLogger(RepositoryFragment::class.java.name)

        @SuppressLint("StaticFieldLeak")
        var LOCAL: RepositoryFragment? = null

        @SuppressLint("StaticFieldLeak")
        var CLOUD: RepositoryFragment? = null

        @SuppressLint("StaticFieldLeak")
        var CURRENT: RepositoryFragment? = null

        @SuppressLint("StaticFieldLeak")
        var OPPOSITE: RepositoryFragment? = null

        fun putLocalAndCloudFragments(localFragment: RepositoryFragment
                                      , cloudFragment: RepositoryFragment) {
            LOCAL = localFragment
            CLOUD = cloudFragment
            CURRENT = localFragment
            OPPOSITE = cloudFragment
        }

        fun swapLocalAndCloud() {
            if (CURRENT === LOCAL) {
                CURRENT = CLOUD
                OPPOSITE = LOCAL
            } else {
                CURRENT = LOCAL
                OPPOSITE = CLOUD
            }
        }
    }

    var nestedScrollView: NestedScrollView? = null

    var recyclerView: RecyclerView? = null

    var recyclerViewAdapter: HistoryRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        this.nestedScrollView = inflater.inflate(R.layout.repository_fragment, container, false)
                as NestedScrollView?
        this.init()
        return this.nestedScrollView
    }

    private fun init() {
        this.initRecyclerView()
    }

    private fun initRecyclerView() {
        val recyclerViewAdapter = HistoryRecyclerViewAdapter()
        val recyclerView = nestedScrollView?.findViewById<RecyclerView>(R.id.history_recycler_view)
        //Инициализируем RecyclerView:
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
        recyclerView?.adapter = recyclerViewAdapter
        this.recyclerView = recyclerView
        this.recyclerViewAdapter = recyclerViewAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(this.recyclerView)
    }

    fun refresh() {

    }
}