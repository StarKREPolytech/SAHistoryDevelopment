package com.historyManagement.provider

import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.HistoryRecyclerViewAdapter
import com.annotations.FuckingStaticSingleton
import com.historyManagement.history.historyData.History
import com.historyManagement.historyManagement.HistoryManager
import com.historyManagement.historyManagement.implementations.CloudHistoryManager
import com.historyManagement.historyManagement.implementations.LocalHistoryManager
import lombok.extern.slf4j.Slf4j
import java.util.*

/**
 * @author Игорь Гулькин 30.04.2018.
 *
 *
 * Класс HistoryManagerProvider является провайдером
 * экземпляров класса [HistoryManager] для
 * [HistoryRecyclerViewAdapter] адаптера через
 * поле currentHistoryManager.
 */

@Slf4j
@FuckingStaticSingleton
class HistoryManagerProvider {

    companion object {
        @JvmField
        var THIS: HistoryManagerProvider? = HistoryManagerProvider()
    }

    private var currentHistoryManager: HistoryManager? = null

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
        val histories = this.currentHistoryManager!!.histories
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
        this.currentHistoryManager = this.localHistoryManager
        this.opposite = this.cloudHistoryManager
    }

    /**
     * swapLocalAndCloud() переключает "локального"
     * и "облачного" менеджера историй.
     */

    fun swapLocalAndCloud() {
        if (this.currentHistoryManager === this.localHistoryManager) {
            this.currentHistoryManager = this.cloudHistoryManager
            this.opposite = this.localHistoryManager
        } else {
            this.currentHistoryManager = this.localHistoryManager
            this.opposite = this.cloudHistoryManager
        }
    }

    /**
     * get()
     *
     * @return текущий history manager.
     */

    fun get(): HistoryManager? {
        return this.currentHistoryManager
    }

    /**
     * selectHistory(final History history) добавляет данную историю
     * в множество выбранных историй.
     *
     * @param position, которую нужно добавить в выбранные истории.
     */

    fun selectHistory(position: Int) {
        val history = this.currentHistoryManager!!.histories[position]
        this.selectHistory(history, this.currentHistoryManager!!)
        if (this.isSynchronizedHistory(history)) {
            this.selectHistory(history, this.opposite!!)
        }
    }

    private fun selectHistory(history: History, historyManager: HistoryManager) {
        historyManager.selectedHistories.plus(history)
        historyManager.refreshSelectedHistory()
    }

    /**
     * selectAllHistories() добавляет все выбранные истории.
     */

    fun selectAllHistories() {
        for (i in 0 until this.currentHistoryManager!!.histories.size) {
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
        val history = this.currentHistoryManager!!.histories[position]
        this.deselectHistory(history, this.currentHistoryManager!!)
        if (this.isSynchronizedHistory(history)) {
            this.deselectHistory(history, this.opposite!!)
        }
    }

    private fun deselectHistory(history: History, historyManager: HistoryManager) {
        historyManager.selectedHistories.minus(history)
        historyManager.refreshSelectedHistory()
    }

    /**
     * deselectAllHistories() удаляет все выбранные истории.
     */

    fun deselectAllHistories() {
        for (i in 0 until this.currentHistoryManager!!.histories.size) {
            this.deselectHistory(i)
        }
    }

    /**
     * addHistories() добавляет новые истории в список историй.
     * Реализовано несколько вариантов.
     */

    fun addHistories(histories: Collection<History>) {
        this.currentHistoryManager!!.histories.addAll(histories)
    }

    fun addHistories(vararg histories: History) {
        val historyCollection = Arrays.asList(*histories)
        this.currentHistoryManager!!.histories.addAll(historyCollection)
    }

    fun addHistory(history: History) {
        this.currentHistoryManager!!.histories.add(history)
    }

    private fun addHistory(history: History, historyManager: HistoryManager) {
        historyManager.histories.add(history)
    }

    /**
     * removeSelectedHistories() удаляет выбранные истории.
     */

    fun removeSelectedHistories() {
        val historySet = this.currentHistoryManager!!.selectedHistories
        val list = ArrayList(historySet)
        for (i in list.indices) {
            val history = list[i]
            this.removeHistory(history)
        }
    }

    /**
     * removeHistory(final History history) удаляет историю из списка.
     *
     * @param history, которую нужно удалить.
     */

    fun removeHistory(history: History) {
        if (this.isSynchronizedHistory(history) && this.synchronizedMode) {
            this.removeHistory(history, this.opposite!!)
        }
        this.removeHistory(history, this.currentHistoryManager!!)
    }

    private fun removeHistory(history: History, historyManager: HistoryManager) {
        historyManager.histories.remove(history)
        historyManager.selectedHistories.minus(history)
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
        val history = this.currentHistoryManager!!.histories[currentPosition]
        return this.synchronize(history)
    }

    private fun synchronize(history: History): Boolean {
        if (!this.opposite!!.hasHistory(history)) {
            val oppositeHistoryList = this.opposite!!.histories
            val headline = history.headline
            val newHeadline = this.checkUniqueHeadlineAndRenameIfNecessary(headline!!, oppositeHistoryList)
            history.headline = newHeadline
            this.addHistory(history, this.opposite!!)
            this.opposite!!.uploadData(history)
            return true
        } else {
            return false
        }
    }

    fun synchronizeAll(): Boolean {
        val currentHistories = this.currentHistoryManager!!.histories
        var hasSynchronized = false
        for (history in currentHistories) {
            if (this.synchronize(history) && !hasSynchronized) {
                hasSynchronized = true
            }
        }
        return hasSynchronized
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

    private fun checkUniqueHeadlineAndRenameIfNecessary(headline: String, otherHistoryList: List<History>): String {
        val headlineBuilder = StringBuilder(headline)
        for (i in otherHistoryList.indices) {
            val otherHeadline = otherHistoryList[i].headline
            if (headlineBuilder.toString() == otherHeadline) {
                headlineBuilder.append(this.currentHistoryManager!!.getRepositoryHeadlinePostfix())
                return checkUniqueHeadlineAndRenameIfNecessary(headlineBuilder.toString(), otherHistoryList)
            }
        }
        return headlineBuilder.toString()
    }

    fun setHeadlineForSelectedHistory(newText: String) {
        if (this.isSynchronizedHistory(this.currentHistoryManager!!.selectedHistory!!)) {
            this.opposite!!.selectedHistory?.headline = newText
        }
        this.currentHistoryManager!!.selectedHistory?.headline = newText
    }

    fun isSynchronizedHistory(history: History): Boolean = this.cloudHistoryManager.histories.contains(history)
            && this.localHistoryManager.histories.contains(history)

    fun hasSynchronizedSelectedHistories(): Boolean {
        val histories = this.currentHistoryManager!!.selectedHistories
        for (history in histories) {
            if (this.isSynchronizedHistory(history)) {
                return true
            }
        }
        return false
    }

    fun sortByAlphabet() {
        this.currentHistoryManager!!.histories.sortBy { it -> it.headline }
    }

    fun sortByDate() {
        this.currentHistoryManager!!.histories.sortedBy { it -> it.description }
    }
}