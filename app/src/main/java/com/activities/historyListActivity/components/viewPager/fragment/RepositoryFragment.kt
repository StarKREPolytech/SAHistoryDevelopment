package com.activities.historyListActivity.components.viewPager.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.starkre.sleepAlertHistory.R
import com.activities.historyListActivity.HistoryListActivity
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.annotations.FuckingStaticSingleton
import java.util.logging.Logger

@FuckingStaticSingleton
class RepositoryFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var THIS: RepositoryFragment? = null
        @JvmStatic
        var log = Logger.getLogger(RepositoryFragment::class.java.name)
    }

    var recyclerViewAdapter: HistoryRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        THIS = this
        val repositoryView = inflater.inflate(R.layout.repository_fragment, container, false)
        this.init(repositoryView)
        return repositoryView
    }

    private fun init(view: View){
        this.initRecyclerView(view)
    }

    private fun initRecyclerView(view: View) {
        this.recyclerViewAdapter = HistoryRecyclerViewAdapter(HistoryListActivity.THIS)
        //Инициализируем RecyclerView:
        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(HistoryListActivity.THIS)
        recyclerView.adapter = this.recyclerViewAdapter
        recyclerView.requestLayout()
    }
}