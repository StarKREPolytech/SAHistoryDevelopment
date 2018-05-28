package com.activities.historyListActivity.components.viewPager.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.annotations.FuckingStaticSingleton
import com.example.starkre.sleepAlertHistory.R
import java.util.logging.Logger

/**
 * Класс RepositoryFragment - это обертка над списком историй RecyclerView в ViewPager.
 */

@FuckingStaticSingleton
class RepositoryFragment : Fragment() {

    companion object {
        @JvmStatic
        var log = Logger.getLogger(RepositoryFragment::class.java.name)


        /**
         * Созадется 2 ViewPagers: одно окно для локального хранилища, другое - для облачного.
         * Так как вкладки переключаются, и чтобы не проверять с какой историей происходит работа,
         * то созданы "текущий" и "запасной" RepositoryFragments, которые будут переключаться.
         */

        @SuppressLint("StaticFieldLeak")
        private var LOCAL: RepositoryFragment? = null

        @SuppressLint("StaticFieldLeak")
        private var CLOUD: RepositoryFragment? = null

        @SuppressLint("StaticFieldLeak")
        var CURRENT: RepositoryFragment? = null

        @SuppressLint("StaticFieldLeak")
        var OPPOSITE: RepositoryFragment? = null

        /**
         * initLocalAndCloudFragments(localFragment: RepositoryFragment
         * , cloudFragment: RepositoryFragment) устанавливает "текущий" и "запасной"
         * RepositoryFragments. Первым всегда устанавливается локальный репозиторий.
         */

        fun initLocalAndCloudFragments(localFragment: RepositoryFragment
                                       , cloudFragment: RepositoryFragment) {
            LOCAL = localFragment
            CLOUD = cloudFragment
            CURRENT = localFragment
            OPPOSITE = cloudFragment
        }

        /**
         * swapLocalAndCloud() меняет местами "текущий" и "запасной" репозитории.
         */

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

    /**
     * 1.) nestedScrollView - ViewPager основан на странице, которая "скроллится";
     * 2.) recyclerView - список историй;
     * 3.) recyclerViewAdapter - адаптер, отслеживающий действия со списком историй.
     */

    private var nestedScrollView: NestedScrollView? = null

    private var recyclerView: RecyclerView? = null

    var recyclerViewAdapter: HistoryRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        this.nestedScrollView = inflater.inflate(R.layout.repository_fragment, container, false)
                as NestedScrollView?
        this.init()
        return this.nestedScrollView
    }

    /**
     * init() инициализирует ViewPager.
     */

    private fun init() {
        this.initRecyclerView()
    }

    /**
     * initRecyclerView() инициализирует список историй.
     */

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

    /**
     * refresh() обновляет ViewPager.
     */

    //Пока пусто, пока обновлять ничего не нужно...
    fun refresh() {
        //Пока пусто...
    }
}