package com.activities.historyListActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import com.example.starkre.sleepAlertHistory.R
import com.annotations.FuckingStaticSingleton
import com.activities.historyListActivity.components.viewPager.adapter.RepositoryPagerAdapter
import com.activities.historyListActivity.components.viewPager.fragment.RepositoryFragment
import com.activities.currentHistoryActivity.CurrentHistoryActivity
import com.historyManagement.history.historyData.History
import com.historyManagement.provider.HistoryManagerProvider
import java.util.ArrayList
import java.util.logging.Logger

@FuckingStaticSingleton
class HistoryListActivity : AppCompatActivity() {

    companion object A {
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var THIS: HistoryListActivity? = null
        @JvmStatic
        val log = Logger.getLogger(HistoryListActivity::class.java.name)
    }

    var tabLayout: TabLayout? = null

    var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_history_list)
        this.init()
    }

    private fun init() {
        this.initToolbar()
        this.initViewPager()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        this.setSupportActionBar(toolbar)
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
                this.add(RepositoryFragment())
                this.add(RepositoryFragment())
            }
        }
        val fragmentAdapter = RepositoryPagerAdapter(this.supportFragmentManager, fragments, titles)
        this.viewPager?.offscreenPageLimit = 1
        this.viewPager?.adapter = fragmentAdapter
        this.tabLayout?.setupWithViewPager(this.viewPager)
        this.tabLayout?.setTabsFromPagerAdapter(fragmentAdapter)
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



    fun refreshScreen(){}

    private fun getPageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float
                                        , positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        }
    }
}