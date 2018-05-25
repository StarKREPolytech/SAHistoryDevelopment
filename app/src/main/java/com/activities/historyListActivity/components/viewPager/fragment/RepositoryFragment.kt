package com.activities.historyListActivity.components.viewPager.fragment

import android.annotation.SuppressLint

import android.os.Bundle
import android.os.IInterface
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.starkre.sleepAlertHistory.R
import com.activities.historyListActivity.HistoryListActivity
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.HistoryAction
import com.annotations.FuckingStaticSingleton
import com.historyManagement.provider.HistoryManagerProvider
import com.historyManagement.utilities.HistoryViewUtils
import java.util.logging.Logger

@FuckingStaticSingleton
class RepositoryFragment : Fragment() {

    companion object {
        @JvmStatic
        var log = Logger.getLogger(RepositoryFragment::class.java.name)

        var LOCAL: RepositoryFragment? = null

        var CLOUD: RepositoryFragment? = null

        var CURRENT : RepositoryFragment? = null

        var OPPOSITE : RepositoryFragment? = null

        fun PUT_LOCAL_AND_CLOUD_FRAGMENTS(localFragment: RepositoryFragment, cloudFragment: RepositoryFragment) {
            LOCAL = localFragment
            CLOUD = cloudFragment
            CURRENT = localFragment
            OPPOSITE = cloudFragment
        }

        fun SWAP_LOCAL_AND_CLOUD() {
            if (CURRENT === LOCAL){
                CURRENT = CLOUD
                OPPOSITE = LOCAL
            } else {
                CURRENT = LOCAL
                OPPOSITE = CLOUD
            }
        }
    }

    var rootView: View? = null

    var recyclerView: RecyclerView? = null

    var recyclerViewAdapter: HistoryRecyclerViewAdapter? = null

    private var descriptionTextView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        this.rootView = inflater.inflate(R.layout.repository_fragment, container, false)
        this.init(this.rootView)
        return this.rootView
    }

    private fun init(view: View?) {
        this.descriptionTextView = view?.findViewById(R.id.history_list_activity_text_description)
        this.initRecyclerView(view)
    }

    private fun initRecyclerView(view: View?) {
        val recyclerViewAdapter = HistoryRecyclerViewAdapter()
        val recyclerView = view?.findViewById<RecyclerView>(R.id.history_recycler_view)
        //Инициализируем RecyclerView:
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(HistoryListActivity.THIS)
        recyclerView?.adapter = recyclerViewAdapter
        recyclerView?.setHasFixedSize(true)
        this.recyclerView = recyclerView
        this.recyclerViewAdapter = recyclerViewAdapter
    }

    fun refresh(){
        this.setDescriptionAboutHistoryVisibility()
    }

    /**
     * setDescriptionAboutHistoryVisibility() устанавливает текстовую
     * информацию об окне истории в центре, если нет историй.
     */

    private fun setDescriptionAboutHistoryVisibility() {
        val historyManager = HistoryManagerProvider.THIS?.get()
        //Если список пустой, то вывести информацию:
        val isEmpty = historyManager?.hasHistories()
        HistoryViewUtils.setVisibility(isEmpty!!, this.descriptionTextView)
    }
}