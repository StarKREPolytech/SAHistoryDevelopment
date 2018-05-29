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

/**
 * Класс HistoryListActivity полностью описывает окно просмотра списка историй.
 * Он включает в себя общий бар Activities, имеет бар работы с историями,
 * бар работы с выбранными историями, всплывающая кнопка "Отмена", клавиатурный помощник,
 * вкладки и списки историй с локальным и облачным репозиториями.
 */

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

    /**
     * 1.) activityMode - режим работы activity;
     * 2.) repositoryTabLayout - вкладки: "Локальное" и "Облачное" хранилище;
     * 3.) viewPager - обертка для списка историй;
     * 4.) bottomNavigationBar - общий бар всех activity;
     * 5.) optionBar - бар работы со всеми историями;
     * 6.) editBar - бар работы с выбранными историями;
     * 7.) popupCancelButton - всплывающая кнопка "Отмена";
     * 8.) keyBoardSupplier - клавиатурный  помощник,
     * который умеет показывать и прятать клавиатуру.
     * 9.) descriptionTextView - текст, который объясняет пользователю,
     * что это за окно, если историй нет.
     */

    var activityMode: HistoryListActivityMode? = START_ACTIVITY_MODE

    private var repositoryTabLayout: TabLayout? = null

    var viewPager: ViewPager? = null

    private val bottomNavigationBar = BottomNavigationBar()

    private val optionBar = OptionBar()

    internal val editBar = EditBar()

    internal val popupCancelButton = PopupCancelButton()

    internal val keyBoardSupplier = KeyBoardSupplier()

    private var descriptionTextView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THIS = this
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_history_list)
        this.init()
        this.refresh()
    }

    /**
     * init() инициализует все части activity.
     */

    private fun init() {
        this.initToolbar()
        this.initViewPager()
        this.bottomNavigationBar.init()
        this.optionBar.init()
        this.editBar.init()
        this.popupCancelButton.init()
        this.descriptionTextView = this.findViewById(R.id.history_list_activity_text_description)
    }

    /**
     * initToolbar() инициализирует верхний бар.
     */

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        this.setSupportActionBar(toolbar)
    }

    /**
     * Вложенный класс BottomNavigationBar
     * оборачивает бар с переходами на разные activity приложения.
     */

    inner class BottomNavigationBar {

        private var bottomNavigationBar: BottomNavigationViewEx? = null

        internal fun init() {
            this.bottomNavigationBar = findViewById(R.id.bottom_navigation_bar)
        }

        /**
         * setBottomNavigationViewVisibility() показывает бар,
         * если установлен режим работы "Просматривать истории".
         * Методы вызывается только тогда, когда происходит вызов метода refresh()
         */

        internal fun setBottomNavigationViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.BROWSING
            HistoryViewUtils.setVisibility(isVisible, this.bottomNavigationBar)
        }
    }

    /**
     * Вложенный класс OptionBar
     * оборачивает бар для работы со всеми историями.
     */

    inner class OptionBar {

        /**
         * 1.) historyOptionRelativeLayout - общий бар;
         * 2.) historySelectingModeImageView - "кнопка-картинка" работы с выбранными историями;
         * 3.) historySyncAllHistoriesImageView "кнопка-картинка" "Синхронизировать все истории";
         * 4.) historySortHistoriesImageView "кнопка-картинка" "Сортировать все истории"
         */

        private var historyOptionRelativeLayout: RelativeLayout? = null

        private var historySelectingModeImageView: ImageView? = null

        private var historySyncAllHistoriesImageView: ImageView? = null

        var historySortHistoriesImageView: ImageView? = null

        /**
         * sortType ууказывает тип сортировки.
         */

        var sortType = SortType.ALPHABET

        /**
         * init() инициазирует бар.
         */

        internal fun init() {
            this.historyOptionRelativeLayout = findViewById(R.id.history_option_bar)
            this.historySelectingModeImageView = findViewById(R.id
                    .history_selecting_mode_image_view)
            this.historySortHistoriesImageView = findViewById(R.id
                    .history_sort_histories_image_view)
            this.historySyncAllHistoriesImageView = findViewById(R.id
                    .history_sync_all_histories_image_view)
        }

        /**
         * refresh() обновляет все элементы бара.
         * Локальный refresh() вызывается только в общем refresh() методе.
         */

        internal fun refresh() {
            this.setOptionViewVisibility()
        }

        /**
         * setOptionViewVisibility() показывает бар, если режим работы установлен в режим
         * работы с всеми историями.
         */

        private fun setOptionViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.SHOW_OPTIONS
            HistoryViewUtils.setVisibility(isVisible, this.historyOptionRelativeLayout)
        }
    }

    /**
     * Крохотный enum SortType указывает тип сортировки.
     */

    enum class SortType {
        ALPHABET, DATE
    }

    /**
     * selectionModeImageViewOnClick(unused: View)
     * обрабатывает нажатие на кнопку "Редактировать выбранные истории".
     *
     * @param unused не используется.
     */

    @XMLProvided(layout = "history_option_view.xml")
    fun selectionModeImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.switchFromBrowsingToSelectingMode()
        RepositoryFragment.OPPOSITE?.recyclerViewAdapter?.switchFromBrowsingToSelectingMode()
        this.refresh()
    }


    /**
     * syncAllImageViewOnClick(unused: View)
     * обрабатывает нажатие на кнопку "Синхронизировать все истории".
     *
     * @param unused не используется.
     */

    @XMLProvided(layout = "history_option_view.xml")
    fun syncAllImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.synchronizeAll()
        this.refresh()
    }

    /**
     * sortImageViewOnClick(unused: View)
     * обрабатывает нажатие на кнопку "Сортировать все истории".
     *
     * @param unused не используется.
     */

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

    /**
     * setEditMode() меняет режим работы activity с режима работы со всеми историями
     * в режим работы просмотра, если был другой режим,
     * то - в режим работы со всеми историями.
     */

    fun setEditMode() {
        if (this.activityMode == HistoryListActivityMode.SHOW_OPTIONS) {
            this.activityMode = HistoryListActivityMode.BROWSING
        } else {
            this.activityMode = HistoryListActivityMode.SHOW_OPTIONS
        }
        this.refresh()
    }

    /**
     * Вложенный класс EditBar
     * оборачивает бар для работы со выбранными историями.
     */

    inner class EditBar {

        /**
         * 1.) historyEditRelativeLayout - бар;
         * 2.) historyDeleteImageView - "кнопка-картинка" "Удалить выбранные истории";
         * 3.) historySyncImageView - "кнопка-картинка" "Синхронизировать выбранные истории";
         * 4.) historyRenameImageView - "кнопка-картинка" "Переименовать историю";
         * 5.) selectAllHistoriesTextView - "кнопка-картинка"
         * "Выбрать все истории" / "Отменить выделение";
         */

        private var historyEditRelativeLayout: RelativeLayout? = null

        private var historyDeleteImageView: ImageView? = null

        private var historySyncImageView: ImageView? = null

        private var historyRenameImageView: ImageView? = null

        internal var selectAllHistoriesTextView: TextView? = null

        /**
         * init() инициализирует бар.
         */

        internal fun init() {
            this.historyEditRelativeLayout = findViewById(R.id.history_edit_bar)
            this.historyDeleteImageView = findViewById(R.id.history_delete_history_image_view)
            this.historySyncImageView = findViewById(R.id.history_sync_history_image_view)
            this.historyRenameImageView = findViewById(R.id.history_rename_history_image_view)
            this.selectAllHistoriesTextView = findViewById(R.id
                    .history_select_all_histories_text_view)
        }

        /**
         * refresh() обновляет все элементы бара.
         * Локальный refresh() вызывается только в общем refresh() методе.
         */

        internal fun refresh() {
            this.setEditViewVisibility()
            this.setSyncImageViewVisibility()
            this.setDeleteImageViewVisibility()
            this.setRenameImageViewVisibility()
            this.setSelectAllHistoriesText()
            this.setSelectAllTextViewVisibility()
        }


        /**
         * setEditViewVisibility() показывает бар,
         * если режим работы - работать с выбранными историями.
         */

        private fun setEditViewVisibility() {
            val isVisible = activityMode == HistoryListActivityMode.SELECTING
            HistoryViewUtils.setVisibility(isVisible, this.historyEditRelativeLayout)
        }

        /**
         * setSyncImageViewVisibility() показывает кнопку "Синхронизовать",
         * если среди выбранных историй есть несинхрониированные или нет выбранных историй.
         */

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

        /**
         * setDeleteImageViewVisibility() показыват кнопку "Удалить", если есть выбранные истории.
         */

        private fun setDeleteImageViewVisibility() {
            val isVisible = !HistoryManagerProvider.THIS?.current?.hasNotSelectedHistories()!!
            HistoryViewUtils.setVisibility(isVisible, this.historyDeleteImageView)
        }

        /**
         * setRenameImageViewVisibility() показывает кнопку "Переименовать историю",
         * если выбрана ровно одна история.
         */

        private fun setRenameImageViewVisibility() {
            val isVisible = HistoryManagerProvider.THIS?.current?.hasOneSelectedHistory()!!
            HistoryViewUtils.setVisibility(isVisible, this.historyRenameImageView)
        }

        /**
         * setSelectAllTextViewVisibility() показывает кнопку выбора / снятия выбора историй,
         * если есть хотя бы одна история в списке.
         */

        private fun setSelectAllTextViewVisibility() {
            val isVisible = HistoryManagerProvider.THIS?.current?.hasHistories()!!
            HistoryViewUtils.setVisibility(isVisible, this.selectAllHistoriesTextView)
        }

        /**
         * setSelectAllHistoriesText() устанавливает техт кнопки выбора историй.
         * Если выбраны все истории, то текст кнопки равен "Отменить выделение".
         * Если нет выбранных историй, -> "Выбрать все".
         */

        fun setSelectAllHistoriesText() {
            val historyManager = HistoryManagerProvider.THIS?.current
            log.info("COUNT SELECTED: " + historyManager?.selectedHistories?.size)
            log.info("COUNT SUMMARY: " + historyManager?.selectedHistories?.size)
            if (historyManager!!.isSelectedAllHistories()) {
                this.selectAllHistoriesTextView?.setText(R.string.history_deselect_all)
            }
            if (historyManager.hasNotSelectedHistories()) {
                this.selectAllHistoriesTextView?.setText(R.string.history_select_all)
            }
        }
    }

    /**
     * deleteImageViewOnClick(unused: View)
     * обрабатывает нажатие на кнопку "Удалить выбранные истории".
     *
     * @param unused не используется.
     */

    @XMLProvided(layout = "history_edit_view.xml")
    fun deleteImageViewOnClick(unused: View) {
        val recyclerViewAdapter = RepositoryFragment.CURRENT?.recyclerViewAdapter
        recyclerViewAdapter?.removeSelectedHistories()
        this.refresh()
    }

    /**
     * syncImageViewOnClick(unused: View)
     * обрабатывает нажатие на кнопку "Синхронизировать выбранные истории".
     *
     * @param unused не используется.
     */

    @XMLProvided(layout = "history_edit_view.xml")
    fun syncImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.synchronizeSelected()
        this.refresh()
    }

    /**
     * renameImageViewOnClick(unused: View)
     * обрабатывает нажатие на кнопку "Переименовать выбранную историю".
     *
     * @param unused не используется.
     */

    @XMLProvided(layout = "history_edit_view.xml")
    fun renameImageViewOnClick(unused: View) {
        RepositoryFragment.CURRENT?.recyclerViewAdapter?.renameSelectedHistory()
        this.keyBoardSupplier.showKeyBoard()
        this.refresh()
    }

    /**
     * selectAllHistories(unused: View)
     * обрабатывает нажатие на кнопку "Выбрать все истории" / "Отменить выбор всех историй".
     *
     * @param unused не используется.
     */

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

    /**
     * Вложенный класс PopupCancelButton
     * оборачивает всплывающую кнопку отмены.
     */

    inner class PopupCancelButton {

        /**
         * 1.) popupCancelButtonRelativeLayout - сама кнопка;
         * 2.) popupCancelButtonTextView - текст кнопки;
         */

        var popupCancelButtonRelativeLayout: RelativeLayout? = null

        var popupCancelButtonTextView: TextView? = null

        /**
         * Init() инициализирует всплывающую кнопку.
         */

        internal fun init() {
            this.popupCancelButtonRelativeLayout = findViewById(R.id
                    .history_popup_cancel_button_relative_layout)
            this.popupCancelButtonTextView = findViewById(R.id
                    .history_popup_cancel_button_text_view)
        }

        /**
         * showPopupCancelButton(cancelActionType: CancelActionType)
         * показывает кнопку на экране.
         *
         * @param cancelActionType какое действие отменить.
         */

        @SuppressLint("PrivateResource")
        fun showPopupCancelButton(cancelActionType: CancelActionType) {
            this.popupCancelButtonRelativeLayout?.visibility = View.VISIBLE
            when (cancelActionType) {
                CancelActionType.CANCEL_REMOVE -> {
                    this.popupCancelButtonTextView?.text = "Отменить удаление"
                    this.popupCancelButtonRelativeLayout?.setOnClickListener({
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

        /**
         * hidePopupCancelButton()
         * прячет кнопку с экрана.
         */

        @SuppressLint("PrivateResource")
        fun hidePopupCancelButton() {
            if (this.popupCancelButtonRelativeLayout?.visibility == View.VISIBLE) {
                this.popupCancelButtonRelativeLayout?.startAnimation(AnimationUtils
                        .loadAnimation(THIS, R.anim.abc_fade_out))
                this.popupCancelButtonRelativeLayout?.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * CancelActionType указывает тип отмены действия.
     */

    enum class CancelActionType {
        CANCEL_REMOVE
    }

    /**
     * initViewPager() инициализирует список историй, обернутый в ViewPager.
     */

    private fun initViewPager() {
        this.repositoryTabLayout = this.findViewById(R.id.tab_layout_main)
        this.viewPager = this.findViewById(R.id.view_pager_main)
        val titles = object : ArrayList<String>() {
            init {
                this.add(getString(R.string.local_repository_tab_title))
                this.add(getString(R.string.cloud_repository_tab_title))
            }
        }
        this.repositoryTabLayout?.addTab(this.repositoryTabLayout!!.newTab().setText(titles[0]))
        this.repositoryTabLayout?.addTab(this.repositoryTabLayout!!.newTab().setText(titles[1]))
        val fragments = object : ArrayList<Fragment>() {
            init {
                val localFragment = RepositoryFragment()
                val cloudFragment = RepositoryFragment()
                this.add(localFragment)
                this.add(cloudFragment)
                RepositoryFragment.initLocalAndCloudFragments(localFragment, cloudFragment)
            }
        }
        val fragmentAdapter = RepositoryPagerAdapter(this.supportFragmentManager, fragments, titles)
        this.viewPager?.offscreenPageLimit = 1
        this.viewPager?.adapter = fragmentAdapter
        this.repositoryTabLayout?.setupWithViewPager(this.viewPager)
        this.repositoryTabLayout?.setTabsFromPagerAdapter(fragmentAdapter) // Знаю, что устарел...
        this.viewPager?.addOnPageChangeListener(this.getPageChangeListener())
    }

    /**
     * goToCurrentHistory(history: History) переходит на окно выбранной истории.
     */

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

    /**
     * refresh() обновляет все элементы окна.
     */

    fun refresh() {
        this.bottomNavigationBar.setBottomNavigationViewVisibility()
        this.optionBar.refresh()
        this.editBar.refresh()
        this.setDescriptionAboutHistoryVisibility()
        RepositoryFragment.CURRENT?.refresh()
    }

    /**
     * getPageChangeListener() возращает обработчик событий при смене вкладок мжду историями.
     */

    private fun getPageChangeListener(): ViewPager.OnPageChangeListener = object
        : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, posOffset: Float, posOffsetPixels: Int) {
            //Ничего...
        }

        /**
         * onPageSelected(position: Int) обрабатывает событие выбора данной вкладки.
         */

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

    /**
     * Вложенный класс KeyBoardSupplier помогает activity
     * с появлением и скрытием клавиатуры на экране.
     */

    inner class KeyBoardSupplier {

        /**
         * keyBoardFocus хранит в себе крайний графический объект,
         * с которым работал пользователь. Используется вместо поля currentFocus.
         */

        private var keyBoardFocus: View? = null

        /**
         * showKeyBoard() показывает клавиатуру.
         */

        internal fun showKeyBoard() {
            this.keyBoardFocus = currentFocus
            val inputMethodService = Context.INPUT_METHOD_SERVICE
            val inputMethodManager = HistoryListActivity.THIS!!
                    .getSystemService(inputMethodService) as InputMethodManager
            inputMethodManager.showSoftInput(this.keyBoardFocus, InputMethodManager.SHOW_IMPLICIT)
        }

        /**
         * showKeyBoard() прячет клавиатуру.
         */

        internal fun hideKeyBoard() {
            val inputMethodService = Context.INPUT_METHOD_SERVICE
            val inputMethodManager = HistoryListActivity.THIS!!
                    .getSystemService(inputMethodService) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.keyBoardFocus!!.windowToken, 0)
        }
    }

    /**
     * onBackPressed() обрабатывает событие при нажатии кнопки "Назад" на телефоне.
     */

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