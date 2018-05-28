package com.historyManagement.historyManagement

import com.annotations.Temporary
import com.example.starkre.sleepAlertHistory.R
import com.historyManagement.history.historyData.History
import java.util.*
import java.util.logging.Logger


/**
 * @author Игорь Гулькин 27.04.2018
 *         <p>
 *         Класс HistoryManager хранит в себе компонены историй,
 *         а также регулирует механизмы, выполняемые на ними,
 *         например: кол-во выделенных историй.
 *         Является хранилищем историй для адаптера RecyclerView.
 *         Именно к {@link HistoryRecyclerViewAdapter}
 *         привязывает ViewHolders к экземплярам класса History и
 *         отображает в RecyclerView.
 */

abstract class HistoryManager {

    private companion object {

        @JvmStatic
        val log = Logger.getLogger(HistoryManager::class.java.name)

        /**
         * Иммитируем данные истории через рандом.
         */

        @Temporary
        @JvmField
        val ЗАХОТЕЛ_ТАКОЙ_РАНДОМ = Random()
    }

    /**
     * 1.) histories - это List, который хранит в себе экземпляры историй;
     * 2.) selectedHistories - это HashSet, который хранит выбранные истории
     * в процессе редактирования.
     */

    val histories: MutableList<History> = mutableListOf()

    val selectedHistories: MutableSet<History> = mutableSetOf()

    var selectedHistory: History? = null

    var removedHistories: MutableList<Pair<History, Int>> = mutableListOf()

    init {
        this.generateHistories()
    }

    /**
     * generateHistories() генерирует истории.
     * Пока данных об историях нет, приходится выкручиваться и выдумывать
     * свои истории с помощью генератора случайных чисел =)
     */

    @Temporary
    private fun generateHistories() {
        val times = ЗАХОТЕЛ_ТАКОЙ_РАНДОМ_НА_ИСТОРИИ()
        for (i in 0..times) {
            val history = History()
            history.headline = "History: " + (times - i)
            this.histories.add(history)
        }
    }

    /**
     * refreshSelectedHistory() устанавливает значение
     * в поле selectedHistory, если в выбранных историях
     * хранится одна история, иначе устанавливается
     * значение null.
     */

    fun refreshSelectedHistory() {
        if (this.selectedHistories.size == 1) {
            this.selectedHistory = this.selectedHistories.iterator().next()
        } else {
            this.selectedHistory = null
        }
    }

    /**
     * hasOneSelectedHistory()
     *
     * @return что выбрана одна история.
     */

    fun hasOneSelectedHistory(): Boolean = this.selectedHistory != null

    /**
     * isSelectedHistory(final History history) проверяет выбрана
     * ли данная история или нет.
     *
     * @param history, которую нужно проверить.
     * @return истина/ложь
     */

    fun isSelectedHistory(history: History): Boolean = this.selectedHistories.contains(history)

    /**
     * isSelectedAllHistories()
     *
     * @return выбраны ли все истории или нет.
     */

    fun isSelectedAllHistories(): Boolean = this.selectedHistories.size == this.histories.size

    /**
     * hasNotSelectedHistories()
     *
     * @return истину, если ни одна история не выбрана.
     */

    fun hasNotSelectedHistories(): Boolean = this.selectedHistories.isEmpty()

    /**
     * logSelectedHistories() выводит на консоль информацию о
     * выделенных элементах и о их количестве.
     */

    @Temporary
    fun logSelectedHistories() {
        log.info("Количество выделенных элементов: " + this.selectedHistories.size)
        for (history in this.selectedHistories) {
            log.info("\n" + history)
        }
    }

    fun hasHistories(): Boolean = !this.histories.isEmpty()


    /**
     * getHistory(final int position)
     *
     * @param position - позиция истории.
     * @return историю.
     */

    fun getHistory(position: Int): History = this.histories[position]

    /**
     * getNumberOfHistories()
     *
     * @return число историй в коллекции.
     */

    fun getNumberOfHistories(): Int = this.histories.size

    /**
     * hasHistory(final History history)
     *
     * @param history - история.
     * @return содержится или нет.
     */

    fun hasHistory(history: History): Boolean = this.histories.contains(history)

    fun getSynchronizedLabel(): Int = R.drawable.history_sync_repository_label_image_view

    /**
     * Абстрактные методы:
     */

    abstract fun getLabel(): Int

    abstract fun getRepositoryHeadlinePostfix(): String

    abstract fun getColor(): Int

    /**
     *
     *
     */

    abstract fun uploadData(history: History)

    abstract fun uploadData(vararg histories: History)

    abstract fun uploadData(historyList: List<History>)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //********************************************************************************************//
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Временные методы рандома.
     */

    @Temporary
    private fun ЗАХОТЕЛ_ТАКОЙ_РАНДОМ_НА_ИСТОРИИ(): Int = ЗАХОТЕЛ_ТАКОЙ_РАНДОМ.nextInt(20) + 17

    @Temporary
    private fun ЗАХОТЕЛ_ТАКОЙ_РАНДОМ_НА_ПОЕЗДКУ_К_БАБУШКЕ(): Int = ЗАХОТЕЛ_ТАКОЙ_РАНДОМ.nextInt(1000)
}