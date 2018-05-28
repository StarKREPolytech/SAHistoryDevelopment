package com.historyManagement.provider

import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.annotations.FuckingStaticSingleton
import com.historyManagement.history.historyData.History
import com.historyManagement.historyManagement.HistoryManager
import com.historyManagement.historyManagement.implementations.CloudHistoryManager
import com.historyManagement.historyManagement.implementations.LocalHistoryManager
import java.util.*
import java.util.logging.Logger

/**
 * @author Игорь Гулькин 30.04.2018.
 *
 *
 * Класс HistoryManagerProvider является провайдером
 * экземпляров класса [HistoryManager] для
 * [HistoryRecyclerViewAdapter] адаптера через
 * поле current.
 */

@FuckingStaticSingleton
class HistoryManagerProvider {

    companion object {
        @JvmField
        var THIS: HistoryManagerProvider? = HistoryManagerProvider()

        @JvmStatic
        var log = Logger.getLogger(HistoryManagerProvider::javaClass.name)
    }

    var current: HistoryManager? = null

    /**
     * getOpposite()
     *
     * @return пассивный history manager.
     */

    var opposite: HistoryManager? = null

    private val localHistoryManager: HistoryManager

    private val cloudHistoryManager: HistoryManager

    /**
     * synchronizedMode - это тумблер, который
     * предназначен для синхронизированных историй.
     *
     *
     * Действия:
     *
     *
     * 1.) Удалить.
     *
     *
     * true: выполняет действие в обоих хранилищах над историей;
     * false: выпольняет действие в текущем хранилище.
     */

    var synchronizedMode = true

    var inScopeHistory: History? = null

    fun isAllSynchronized(): Boolean {
        val histories = this.current!!.histories
        for (history in histories) {
            if (!this.isSynchronizedHistory(history)) {
                return false
            }
        }
        return true
    }

    init {
        //Инициализируем менеджеры:
        this.localHistoryManager = LocalHistoryManager()
        this.cloudHistoryManager = CloudHistoryManager()
        //Устанавливаем текущий и альтернативный history manager.
        //Текущий изначально локальный менеджер:
        this.current = this.localHistoryManager
        this.opposite = this.cloudHistoryManager
    }

    /**
     * swapLocalAndCloud() переключает "локального"
     * и "облачного" менеджера историй.
     */

    fun swapLocalAndCloud() {
        if (this.current === this.localHistoryManager) {
            this.current = this.cloudHistoryManager
            this.opposite = this.localHistoryManager
        } else {
            this.current = this.localHistoryManager
            this.opposite = this.cloudHistoryManager
        }
    }

    /**
     * selectHistory(final History history) добавляет данную историю
     * в множество выбранных историй.
     *
     * @param position, которую нужно добавить в выбранные истории.
     */

    fun selectHistory(position: Int) {
        log.info("Select history on position $position")
        val history = this.current!!.histories[position]
        log.info("Take $history")
        this.selectHistory(history, this.current!!)
        if (this.isSynchronizedHistory(history)) {
            this.selectHistory(history, this.opposite!!)
        }
    }

    private fun selectHistory(history: History, historyManager: HistoryManager) {
        log.info("Before selected histories are ${historyManager.selectedHistories}")
        historyManager.selectedHistories.add(history)
        log.info("After selected histories are ${historyManager.selectedHistories}")
        historyManager.refreshSelectedHistory()
    }

    /**
     * selectAllHistories() добавляет все выбранные истории.
     */

    fun selectAllHistories() {
        for (i in 0 until this.current!!.histories.size) {
            this.selectHistory(i)
        }
    }

    /**
     * deselectHistory(final History history) удаляет данную историю
     * из множества выбранных историй.
     *
     * @param position, которую нужно убрать из выбранных историй.
     */

    fun deselectHistory(position: Int) {
        val history = this.current!!.histories[position]
        this.deselectHistory(history, this.current!!)
        if (this.isSynchronizedHistory(history)) {
            this.deselectHistory(history, this.opposite!!)
        }
    }

    private fun deselectHistory(history: History, historyManager: HistoryManager) {
        historyManager.selectedHistories.remove(history)
        historyManager.refreshSelectedHistory()
    }

    /**
     * deselectAllHistories() удаляет все выбранные истории.
     */

    fun deselectAllHistories() {
        for (i in 0 until this.current!!.histories.size) {
            this.deselectHistory(i)
        }
    }

    /**
     * addHistories() добавляет новые истории в список историй.
     * Реализовано несколько вариантов.
     */

    fun addHistories(histories: Collection<History>) {
        this.current!!.histories.addAll(histories)
    }

    fun addHistories(vararg histories: History) {
        val historyCollection = Arrays.asList(*histories)
        this.current!!.histories.addAll(historyCollection)
    }

    fun addHistory(history: History) {
        this.current!!.histories.add(history)
    }

    private fun addHistory(history: History, historyManager: HistoryManager) {
        historyManager.histories.add(history)
    }

    /**
     * removeSelectedHistories() удаляет выбранные истории.
     */

    fun removeSelectedHistories() {
        this.lastWithRemovedHistories = this.current
        val historySet = this.lastWithRemovedHistories!!.selectedHistories
        val removedList = mutableListOf<History>()
        removedList.addAll(historySet)
        this.indexHistories(removedList)

        for (i in removedList.indices) {
            val history = removedList[i]
            this.removeHistory(history)
        }
    }

    private fun indexHistories(removedList: MutableList<History>) {
        for (history in removedList) {
            val position = this.current?.histories?.indexOf(history)
            this.current?.removedHistories?.add(Pair(history, position!!))
        }
    }

    private var lastWithRemovedHistories: HistoryManager? = null

    fun clearRemovedHistories() = this.lastWithRemovedHistories?.removedHistories?.clear()

    fun restoreRemovedHistories() {
        val removedHistories = this.lastWithRemovedHistories!!.removedHistories
        removedHistories.sortBy { it.second }
        for (historyVsPosition in removedHistories) {
            val history = historyVsPosition.first
            val position = historyVsPosition.second
            this.lastWithRemovedHistories?.histories?.add(position, history)
        }
        this.clearRemovedHistories()
    }

    /**
     * removeHistory(final History history) удаляет историю из списка.
     *
     * @param history, которую нужно удалить.
     */

    private fun removeHistory(history: History) {
        if (this.isSynchronizedHistory(history) && this.synchronizedMode) {
            this.removeHistory(history, this.opposite!!)
        }
        this.removeHistory(history, this.current!!)
    }

    private fun removeHistory(history: History, historyManager: HistoryManager) {
        historyManager.histories.remove(history)
        historyManager.selectedHistories.remove(history)
        historyManager.refreshSelectedHistory()
    }

    /**
     * synchronize() синхронизирует историю в обоих
     * хранилищах.
     *
     * @param currentPosition - номер позиции.
     * @return истину / ложь.
     */

    fun synchronize(currentPosition: Int): Boolean {
        val history = this.current!!.histories[currentPosition]
        return this.synchronize(history)
    }

    private fun synchronize(history: History): Boolean {
        return if (!this.opposite!!.hasHistory(history)) {
            val oppositeHistoryList = this.opposite!!.histories
            val headline = history.headline
            val newHeadline = this.checkUniqueHeadlineAndRenameIfNecessary(headline!!
                    , oppositeHistoryList)
            history.headline = newHeadline
            this.addHistory(history, this.opposite!!)
            this.opposite!!.uploadData(history)
            true
        } else {
            false
        }
    }

    fun synchronizeAll(): Boolean {
        val currentHistories = this.current!!.histories
        var hasSynchronized = false
        for (history in currentHistories) {
            if (this.synchronize(history) && !hasSynchronized) {
                hasSynchronized = true
            }
        }
        return hasSynchronized
    }

    fun synchronizeSelected() = this.current!!.selectedHistories.forEach { history ->
        this.synchronize(history)
    }

    /**
     * String checkUniqueHeadlineAndRenameIfNecessary(final String headline
     * , final List<History> otherHistoryList) проверяет, есть ли история в
     * противоположном хранилище с таким же именем, если да, то добавляет к
     * синхронизируемой истории постфикс репозитория, откуда история была
     * выгружена.
     *
     *
     * Пример:
     * Заголовок на локальном хранилище: "Кемерово-Москва";
     * Заголовок на облачном хранилище: "Кемерово-Москва".
     *
     *
     * При выгрузки локальное -> облачное будут две истории
     * в облачном и одна в локальном соответсвенно:
     *
     *
     * Локальное хранилище: "Кемерово-Москва(Локальный)"
     * Облачное хранилище: "Кемерово-Москва", "Кемерово-Москва(Локальный)"
     *
     * @param headline         - заголовок синхронизируемой истории;
     * @param otherHistoryList - список историй из противоположного
     * хранилища.
     * @return новый заголовок синхронизируемой истории.
    </History> */

    private fun checkUniqueHeadlineAndRenameIfNecessary(headline: String
                                                        , otherHistoryList: List<History>): String {
        val headlineBuilder = StringBuilder(headline)
        for (i in otherHistoryList.indices) {
            val otherHeadline = otherHistoryList[i].headline
            if (headlineBuilder.toString() == otherHeadline) {
                headlineBuilder.append(this.current!!.getRepositoryHeadlinePostfix())
                return checkUniqueHeadlineAndRenameIfNecessary(headlineBuilder.toString()
                        , otherHistoryList)
            }
        }
        return headlineBuilder.toString()
    }

    fun setHeadlineForSelectedHistory(newText: String) {
        if (this.isSynchronizedHistory(this.current!!.selectedHistory!!)) {
            this.opposite!!.selectedHistory?.headline = newText
        }
        this.current!!.selectedHistory?.headline = newText
    }

    fun isSynchronizedHistory(history: History): Boolean = this.cloudHistoryManager.histories
            .contains(history) && this.localHistoryManager.histories.contains(history)

    fun hasSynchronizedSelectedHistories(): Boolean {
        val histories = this.current!!.selectedHistories
        for (history in histories) {
            if (this.isSynchronizedHistory(history)) {
                return true
            }
        }
        return false
    }

    fun sortByAlphabet() = this.current!!.histories.sortBy { it.headline }

    fun sortByDate() = this.current!!.histories.sortBy { it.description }
}