package com.activities.historyListActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.starkre.sleepAlertHistory.R
import com.annotations.FuckingStaticSingleton
import com.activities.historyListActivity.components.viewPager.adapter.RepositoryPagerAdapter
import com.activities.historyListActivity.components.viewPager.fragment.RepositoryFragment
import com.activities.currentHistoryActivity.CurrentHistoryActivity
import com.activities.historyListActivity.mode.HistoryListActivityMode
import com.annotations.XMLProvided
import com.historyManagement.history.historyData.History
import com.historyManagement.provider.HistoryManagerProvider
import com.historyManagement.utilities.HistoryViewUtils
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import java.util.ArrayList
import java.util.logging.Logger

@FuckingStaticSingleton
class HistoryListActivity : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var THIS: HistoryListActivity? = null
        @JvmStatic
        val log = Logger.getLogger(HistoryListActivity::class.java.name)
    }

    /**
     * Изначально адаптер находится в режиме просмотра историй.
     */

    private val START_ACTIVITY_MODE = HistoryListActivityMode.BROWSING

    var activityMode: HistoryListActivityMode? = START_ACTIVITY_MODE

    var tabLayout: TabLayout? = null

    var viewPager: ViewPager? = null

    var bottomNavigationBar: BottomNavigationViewEx? = null

    var optionBar = OptionBar()

    override fun onCreate(savedInstanceState: Bundle?) {
        THIS = this
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_history_list)
        this.init()
    }

    private fun init() {
        this.initToolbar()
        this.initViewPager()
        this.initBottomNavigationView()
        this.optionBar.initOptionsBar()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        this.setSupportActionBar(toolbar)
    }

    private fun initBottomNavigationView() {
        this.bottomNavigationBar = this.findViewById(R.id.bottom_navigation_bar)
    }

    inner class OptionBar {

        var historyOptionRelativeLayout: RelativeLayout? = null

        private var historySelectingModeImageView: ImageView? = null

        private var historySortHistoriesImageView: ImageView? = null

        private var historySyncAllHistoriesImageView: ImageView? = null

        internal fun initOptionsBar() {
            this.historyOptionRelativeLayout = findViewById(R.id.history_options_bar)
            this.historySelectingModeImageView = findViewById(R.id
                    .history_selecting_mode_image_view)
            this.historySortHistoriesImageView = findViewById(R.id
                    .history_sort_histories_image_view)
            this.historySyncAllHistoriesImageView = findViewById(R.id
                    .history_sync_all_histories_image_view)
        }
    }

    @XMLProvided(layout = "history_options_view.xml")
    fun historySelectionModeOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.switchFromBrowsingToSelectingMode()
        RepositoryFragment.OPPOSITE?.recyclerViewAdapter?.switchFromBrowsingToSelectingMode()
        this.refreshScreen()
    }

    private fun initViewPager() {
        this.tabLayout = this.findViewById(R.id.tab_layout_main)
        this.viewPager = this.findViewById(R.id.view_pager_main)
        val titles = object : ArrayList<String>() {
            init {
                this.add(getString(R.string.tab_title_main_1))
                this.add(getString(R.string.tab_title_main_2))
            }
        }
        this.tabLayout?.addTab(this.tabLayout!!.newTab().setText(titles[0]))
        this.tabLayout?.addTab(this.tabLayout!!.newTab().setText(titles[1]))
        val fragments = object : ArrayList<Fragment>() {
            init {
                val localFragment = RepositoryFragment()
                val cloudFragment = RepositoryFragment()
                this.add(localFragment)
                this.add(cloudFragment)
                RepositoryFragment.PUT_LOCAL_AND_CLOUD_FRAGMENTS(localFragment, cloudFragment)
            }
        }
        val fragmentAdapter = RepositoryPagerAdapter(this.supportFragmentManager, fragments, titles)
        this.viewPager?.offscreenPageLimit = 1
        this.viewPager?.adapter = fragmentAdapter
        this.tabLayout?.setupWithViewPager(this.viewPager)
        this.tabLayout?.setTabsFromPagerAdapter(fragmentAdapter) // Знаю, что устарел
        this.viewPager?.addOnPageChangeListener(this.getPageChangeListener())
    }

    fun goToCurrentHistory(history: History) {
        //Переходим в выбранную историю --->
        HistoryManagerProvider.THIS?.inScopeHistory = history
        this.startActivity(Intent(this, CurrentHistoryActivity::class.java))
        val analyser = history.dataAnalyser
        log.info("DISTANCE INTERVAL: " + analyser.distanceInterval)
        log.info("TIME INTERVAL: " + analyser.timeInterval)
        log.info("WARNINGS: " + analyser.getWarningNumber() + "")
        log.info("LOW: " + analyser.low)
        log.info("MEDIUM: " + analyser.medium)
        log.info("HIGH: " + analyser.high)
        log.info("CRITICAL: " + analyser.critical)
        log.info("DATA SIZE: " + analyser.inputDataList.size)
    }

    fun refreshScreen() {
        this.setBottomNavigationViewVisibility()
        this.setOptionsViewVisibility()
        RepositoryFragment.CURRENT?.refresh()
    }

    private fun getPageChangeListener(): ViewPager.OnPageChangeListener = object
        : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, posOffset: Float, posOffsetPixels: Int) {
            //Ничего...
        }

        @SuppressLint("PrivateResource")
        override fun onPageSelected(position: Int) {
            //Меняем местами менеджеров:
            HistoryManagerProvider.THIS?.swapLocalAndCloud()
            RepositoryFragment.SWAP_LOCAL_AND_CLOUD()
            val currentRepositoryFragment = RepositoryFragment.CURRENT
            val adapter = currentRepositoryFragment?.recyclerViewAdapter
            //Говорим адаптеру, что данные поменялись, и он перепривязывает холдеры:
            adapter?.notifyDataSetChanged()
            //Говорим адаптеру, чтобы он переключился в режим просмотра.
            adapter?.switchFromSelectingToBrowsingMode()
            HistoryListActivity.THIS?.refreshScreen()
        }

        override fun onPageScrollStateChanged(state: Int) {
            //Ничего...
        }
    }

    private fun setBottomNavigationViewVisibility() {
        val isVisible = this.activityMode == HistoryListActivityMode.BROWSING
        HistoryViewUtils.setVisibility(isVisible, this.bottomNavigationBar)
    }

    private fun setOptionsViewVisibility() {
        val isVisible = this.activityMode == HistoryListActivityMode.SHOW_OPTIONS
        HistoryViewUtils.setVisibility(isVisible, this.optionBar.historyOptionRelativeLayout)
    }

    fun showOptionsView() {
        if (this.activityMode == HistoryListActivityMode.SHOW_OPTIONS) {
            this.activityMode = HistoryListActivityMode.BROWSING
        } else {
            this.activityMode = HistoryListActivityMode.SHOW_OPTIONS
        }
        this.refreshScreen()
    }

    override fun onBackPressed() {
        if (this.activityMode == HistoryListActivityMode.SHOW_OPTIONS) {
            this.activityMode = HistoryListActivityMode.BROWSING
        } else {
            super.onBackPressed()
        }
        this.refreshScreen()
    }
}