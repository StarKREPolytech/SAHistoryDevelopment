package com.activities.historyListActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
        val log: Logger = Logger.getLogger(HistoryListActivity::class.java.name)

        /**
         * Изначально адаптер находится в режиме просмотра историй.
         */

        private val START_ACTIVITY_MODE = HistoryListActivityMode.BROWSING
    }

    var activityMode: HistoryListActivityMode? = START_ACTIVITY_MODE

    private var tabLayout: TabLayout? = null

    var viewPager: ViewPager? = null

    private val bottomNavigationBar = BottomNavigationBar()

    private val optionBar = OptionBar()

    internal val editBar = EditBar()

    internal val keyBoardSupplier = KeyBoardSupplier()

    internal val popupCancelButton = PopupCancelButton()

    private var descriptionTextView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THIS = this
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_history_list)
        this.init()
        this.refresh()
    }

    private fun init() {
        this.initToolbar()
        this.initViewPager()
        this.bottomNavigationBar.init()
        this.optionBar.init()
        this.editBar.init()
        this.popupCancelButton.init()
        this.descriptionTextView = this.findViewById(R.id.history_list_activity_text_description)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        this.setSupportActionBar(toolbar)
    }

    inner class BottomNavigationBar {

        private var bottomNavigationBar: BottomNavigationViewEx? = null

        internal fun init() {
            this.bottomNavigationBar = findViewById(R.id.bottom_navigation_bar)
        }

        internal fun setBottomNavigationViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.BROWSING
            HistoryViewUtils.setVisibility(isVisible, this.bottomNavigationBar)
        }
    }

    inner class OptionBar {

        private var historyOptionRelativeLayout: RelativeLayout? = null

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

        internal fun refresh() {
            this.setOptionViewVisibility()
        }

        private fun setOptionViewVisibility() {
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
        this.refresh()
    }

    @XMLProvided(layout = "history_option_view.xml")
    fun syncAllImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.synchronizeAll()
        this.refresh()
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
        this.refresh()
    }

    fun showOptionsView() {
        if (this.activityMode == HistoryListActivityMode.SHOW_OPTIONS) {
            this.activityMode = HistoryListActivityMode.BROWSING
        } else {
            this.activityMode = HistoryListActivityMode.SHOW_OPTIONS
        }
        this.refresh()
    }

    inner class EditBar {

        private var historyEditRelativeLayout: RelativeLayout? = null

        private var historyDeleteImageView: ImageView? = null

        private var historySyncImageView: ImageView? = null

        private var historyRenameImageView: ImageView? = null

        internal var selectAllHistoriesTextView: TextView? = null

        internal fun init() {
            this.historyEditRelativeLayout = findViewById(R.id.history_edit_bar)
            this.historyDeleteImageView = findViewById(R.id.history_delete_history_image_view)
            this.historySyncImageView = findViewById(R.id.history_sync_history_image_view)
            this.historyRenameImageView = findViewById(R.id.history_rename_history_image_view)
            this.selectAllHistoriesTextView = findViewById(R.id
                    .history_select_all_histories_text_view)
        }

        internal fun refresh() {
            this.setEditViewVisibility()
            this.setSyncImageViewVisibility()
            this.setDeleteImageViewVisibility()
            this.setRenameImageViewVisibility()
            this.setSelectAllHistoriesText()
            this.setSelectAllTextViewVisibility()
        }

        private fun setEditViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.SELECTING
            HistoryViewUtils.setVisibility(isVisible, this.historyEditRelativeLayout)
        }

        private fun setSyncImageViewVisibility() {
            val historyManagerProvider = HistoryManagerProvider.THIS
            val historyManager = historyManagerProvider?.current
            val selectedHistorySet = historyManager?.selectedHistories
            for (selectedHistory in selectedHistorySet!!) {
                if (!historyManagerProvider.isSynchronizedHistory(selectedHistory)) {
                    HistoryViewUtils.setVisibility(true, this.historySyncImageView)
                    return
                }
            }
            HistoryViewUtils.setVisibility(false, this.historySyncImageView)
        }

        private fun setDeleteImageViewVisibility() {
            val isVisible = !HistoryManagerProvider.THIS?.current?.hasNotSelectedHistories()!!
            HistoryViewUtils.setVisibility(isVisible, this.historyDeleteImageView)
        }

        private fun setRenameImageViewVisibility() {
            val isVisible = HistoryManagerProvider.THIS?.current?.hasOneSelectedHistory()!!
            HistoryViewUtils.setVisibility(isVisible, this.historyRenameImageView)
        }

        private fun setSelectAllTextViewVisibility(){
            val isVisible = HistoryManagerProvider.THIS?.current?.hasHistories()!!
            HistoryViewUtils.setVisibility(isVisible, this.selectAllHistoriesTextView)
        }

        fun setSelectAllHistoriesText() {
            val historyManager = HistoryManagerProvider.THIS?.current
            log.info("COUNT SELECTED: " + historyManager?.selectedHistories?.size)
            log.info("COUNT SUMMARY: " + historyManager?.selectedHistories?.size)
            if (historyManager!!.isSelectedAllHistories()) {
                log.info("ALL SELECTED!!!")
                this.selectAllHistoriesTextView?.setText(R.string.history_deselect_all)
            }
            if (historyManager.hasNotSelectedHistories()) {
                log.info("ALL DESELECTED!!!")
                this.selectAllHistoriesTextView?.setText(R.string.history_select_all)
            }
        }

    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun deleteImageViewOnClick(unused: View) {
        val recyclerViewAdapter = RepositoryFragment.CURRENT?.recyclerViewAdapter
        recyclerViewAdapter?.removeSelectedHistories()
        this.refresh()
    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun syncImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.synchronizeSelected()
        this.refresh()
    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun renameImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.renameSelectedHistory()
        this.keyBoardSupplier.showKeyBoard()
        this.refresh()
    }

    @XMLProvided(layout = "history_edit_view.xml")
    fun selectAllHistories(unused: View) {
        val adapter = RepositoryFragment.CURRENT?.recyclerViewAdapter
        //Немного неадекватная проверка:
        val historySelectAllString = this.getString(R.string.history_select_all)
        val textView = this.editBar.selectAllHistoriesTextView
        if (textView?.text == historySelectAllString) {
            adapter?.selectAllHistories()
            textView?.setText(R.string.history_deselect_all)
        } else {
            adapter?.deselectAllHistories()
            textView?.setText(R.string.history_select_all)
        }
        this.refresh()
    }

    inner class PopupCancelButton {

        var popupCancelButtonRelativeLayout: RelativeLayout? = null

        var popupCancelButtonTextView: TextView? = null

        var notPressedCancelButton = true

        internal fun init() {
            this.popupCancelButtonRelativeLayout = findViewById(R.id
                    .history_popup_cancel_button_relative_layout)
            this.popupCancelButtonTextView = findViewById(R.id
                    .history_popup_cancel_button_text_view)
        }

        @SuppressLint("PrivateResource")
        fun showPopupCancelButton(cancelActionType: CancelActionType) {
            this.popupCancelButtonRelativeLayout?.visibility = View.VISIBLE
            when (cancelActionType) {
                CancelActionType.CANCEL_REMOVE -> {
                    this.popupCancelButtonTextView?.text = "Отменить удаление"
                    this.popupCancelButtonRelativeLayout?.setOnClickListener({
                        popupCancelButton.notPressedCancelButton = false
                        HistoryManagerProvider.THIS?.restoreRemovedHistories()
                        RepositoryFragment.CURRENT?.recyclerViewAdapter?.notifyDataSetChanged()
                        hidePopupCancelButton()
                        refresh()
                    })
                }
            }
            this.popupCancelButtonRelativeLayout?.startAnimation(AnimationUtils.loadAnimation(THIS
                    , R.anim.abc_fade_in))
        }

        @SuppressLint("PrivateResource")
        fun hidePopupCancelButton() {
            if (this.popupCancelButtonRelativeLayout?.visibility == View.VISIBLE) {
                this.popupCancelButtonRelativeLayout?.startAnimation(AnimationUtils
                        .loadAnimation(THIS, R.anim.abc_fade_out))
                this.popupCancelButtonRelativeLayout?.visibility = View.INVISIBLE
            }
        }
    }

    enum class CancelActionType {
        CANCEL_REMOVE
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
        this.tabLayout?.setTabsFromPagerAdapter(fragmentAdapter) // Знаю, что устарел...
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

    fun refresh() {
        this.bottomNavigationBar.setBottomNavigationViewVisibility()
        this.optionBar.refresh()
        this.editBar.refresh()
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
            refresh()
        }

        override fun onPageScrollStateChanged(state: Int) {
            //Ничего...
        }
    }

    inner class KeyBoardSupplier {

        private var keyBoardFocus: View? = null

        internal fun showKeyBoard() {
            this.keyBoardFocus = currentFocus
            log.info("VIEW: ${this.keyBoardFocus}")
            val inputMethodService = Context.INPUT_METHOD_SERVICE
            val inputMethodManager = HistoryListActivity.THIS!!
                    .getSystemService(inputMethodService) as InputMethodManager
            //InputMethodManager точно не null:
            inputMethodManager.showSoftInput(this.keyBoardFocus, InputMethodManager.SHOW_IMPLICIT)
        }

        fun hideKeyBoard() {
            log.info("VIEW: ${this.keyBoardFocus}")
            val inputMethodService = Context.INPUT_METHOD_SERVICE
            val inputMethodManager = HistoryListActivity.THIS!!
                    .getSystemService(inputMethodService) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.keyBoardFocus!!.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        val currentAdapter = RepositoryFragment.CURRENT?.recyclerViewAdapter
        val oppositeAdapter = RepositoryFragment.OPPOSITE?.recyclerViewAdapter
        when (this.activityMode) {
            HistoryListActivityMode.SHOW_OPTIONS -> this.activityMode = HistoryListActivityMode
                    .BROWSING
            HistoryListActivityMode.SELECTING -> {
                this.activityMode = HistoryListActivityMode.SHOW_OPTIONS
                currentAdapter?.switchFromSelectingToBrowsingMode()
                oppositeAdapter?.switchFromSelectingToBrowsingMode()
            }
            else -> super.onBackPressed()
        }
        this.refresh()
    }

    /**
     * setDescriptionAboutHistoryVisibility() устанавливает текстовую
     * информацию об окне истории в центре, если нет историй.
     */

    private fun setDescriptionAboutHistoryVisibility() {
        val historyManager = HistoryManagerProvider.THIS?.current
        //Если список пустой, то вывести информацию:
        val isEmpty = historyManager?.hasHistories()
        HistoryViewUtils.setVisibility(!isEmpty!!, this.descriptionTextView)
    }
}