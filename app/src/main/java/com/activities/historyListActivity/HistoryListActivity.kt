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

    val bottomNavigationBar = BottomNavigationBar()

    val optionBar = OptionBar()

    val editBar = EditBar()

    val dialogWindow = DialogWindow()

    private var descriptionTextView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THIS = this
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_history_list)
        this.init()
    }

    private fun init() {
        this.initToolbar()
        this.initViewPager()
        this.bottomNavigationBar.init()
        this.optionBar.init()
        this.editBar.init()
        this.dialogWindow.init()
        this.descriptionTextView = this.findViewById(R.id.history_list_activity_text_description)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        this.setSupportActionBar(toolbar)
    }

    inner class BottomNavigationBar {

        var bottomNavigationBar: BottomNavigationViewEx? = null

        internal fun init() {
            this.bottomNavigationBar = findViewById(R.id.bottom_navigation_bar)
        }

        internal fun setBottomNavigationViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.BROWSING
            HistoryViewUtils.setVisibility(isVisible, this.bottomNavigationBar)
        }
    }

    inner class OptionBar {

        var historyOptionRelativeLayout: RelativeLayout? = null

        private var historySelectingModeImageView: ImageView? = null

        private var historySyncAllHistoriesImageView: ImageView? = null

        var historySortHistoriesImageView: ImageView? = null

        var sortType = SortType.ALPHABET

        internal fun init() {
            this.historyOptionRelativeLayout = findViewById(R.id.history_option_bar)
            this.historySelectingModeImageView = findViewById(R.id
                    .history_selecting_mode_image_view)
            this.historySortHistoriesImageView = findViewById(R.id
                    .history_sort_histories_image_view)
            this.historySyncAllHistoriesImageView = findViewById(R.id
                    .history_sync_all_histories_image_view)
        }

        internal fun setOptionViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.SHOW_OPTIONS
            HistoryViewUtils.setVisibility(isVisible, this.historyOptionRelativeLayout)
        }
    }

    enum class SortType {
        ALPHABET, DATE
    }

    @XMLProvided(layout = "history_option_view.xml")
    fun selectionModeImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.switchFromBrowsingToSelectingMode()
        RepositoryFragment.OPPOSITE?.recyclerViewAdapter?.switchFromBrowsingToSelectingMode()
        this.refreshScreen()
    }

    @XMLProvided(layout = "history_option_view.xml")
    fun syncAllImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.synchronizeAll()
        this.refreshScreen()
    }

    @XMLProvided(layout = "history_option_view.xml")
    fun sortImageViewOnClick(unused: View) {
        when (this.optionBar.sortType) {
            SortType.ALPHABET -> {
                RepositoryFragment.CURRENT?.recyclerViewAdapter?.sortHistoryListByAlphabet()
                this.optionBar.sortType = SortType.DATE
                this.optionBar.historySortHistoriesImageView?.setImageResource(R.drawable
                        .ic_history_sort_histories_by_date_image_view)
            }
            SortType.DATE -> {
                RepositoryFragment.CURRENT?.recyclerViewAdapter?.sortHistoryListByDate()
                this.optionBar.sortType = SortType.ALPHABET
                this.optionBar.historySortHistoriesImageView?.setImageResource(R.drawable
                        .ic_history_sort_histories_by_alphabet_image_view)
            }
        }
        this.refreshScreen()
    }

    inner class EditBar {

        var historyEditRelativeLayout: RelativeLayout? = null

        private var historyDeleteImageView: ImageView? = null

        private var historySyncImageView: ImageView? = null

        var historyRenameImageView: ImageView? = null

        internal fun init() {
            this.historyEditRelativeLayout = findViewById(R.id.history_edit_bar)
            this.historyDeleteImageView = findViewById(R.id.history_delete_history_image_view)
            this.historySyncImageView = findViewById(R.id.history_sync_history_image_view)
            this.historyRenameImageView = findViewById(R.id.history_rename_history_image_view)
        }

        internal fun setEditViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.SELECTING
            HistoryViewUtils.setVisibility(isVisible, this.historyEditRelativeLayout)
        }
    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun deleteImageViewOnClick(unused: View) {
        val recyclerViewAdapter = RepositoryFragment.CURRENT?.recyclerViewAdapter
//        final HistoryPopUpFrame popUpPanel = this.parent.getHistoryPopUpFrame();
//        final RelativeLayout relativePopUpLayoutPanel = popUpPanel.getRelativePopUpLayoutPanel();
//        return view -> {
//            final boolean hasSynchronized = HistoryManagerProvider.THIS
//                    .hasSynchronizedSelectedHistories();
//            if (hasSynchronized) {
//                adapter.setCurrentAction(ActionType.REMOVE);
//                adapter.setCurrentFilter(FilterType.SYNCHRONIZED);
//                popUpPanel.buildMessage(ActionType.REMOVE, FilterType.SYNCHRONIZED);
//                relativePopUpLayoutPanel.setVisibility(View.VISIBLE);
//            } else {
//                adapter.removeSelectedHistories();
//            }
//            this.parent.refreshScreen();
//        }
    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun syncImageViewOnClick(unused: View) {

    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun renameImageViewOnClick(unused: View) {

    }

    inner class DialogWindow {

        internal fun init(){

        }
    }

    private fun initViewPager() {
        this.tabLayout = this.findViewById(R.id.tab_layout_main)
        this.viewPager = this.findViewById(R.id.view_pager_main)
        val titles = object : ArrayList<String>() {
            init {
                this.add(getString(R.string.local_repository_tab_title))
                this.add(getString(R.string.cloud_repository_tab_title))
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
                RepositoryFragment.putLocalAndCloudFragments(localFragment, cloudFragment)
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
        this.bottomNavigationBar.setBottomNavigationViewVisibility()
        this.optionBar.setOptionViewVisibility()
        this.editBar.setEditViewVisibility()
        this.setDescriptionAboutHistoryVisibility()
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
            RepositoryFragment.swapLocalAndCloud()
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

    /**
     * setDescriptionAboutHistoryVisibility() устанавливает текстовую
     * информацию об окне истории в центре, если нет историй.
     */

    private fun setDescriptionAboutHistoryVisibility() {
        val historyManager = HistoryManagerProvider.THIS?.current
        //Если список пустой, то вывести информацию:
        val isEmpty = historyManager?.hasHistories()
        HistoryViewUtils.setVisibility(isEmpty!!, this.descriptionTextView)
    }
}