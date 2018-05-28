package com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.activities.historyListActivity.HistoryListActivity
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.ActionType
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.FilterType
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.HistoryAction
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.HistoryFilter
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.viewHolder.HistoryViewHolder
import com.activities.historyListActivity.mode.HistoryListActivityMode
import com.example.starkre.sleepAlertHistory.R
import com.historyManagement.history.historyData.History
import com.historyManagement.historyManagement.HistoryManager
import com.historyManagement.historyManagement.implementations.CloudHistoryManager
import com.historyManagement.historyManagement.implementations.LocalHistoryManager
import com.historyManagement.provider.HistoryManagerProvider
import com.historyManagement.utilities.HistoryViewUtils
import es.dmoral.toasty.Toasty
import java.util.*
import java.util.logging.Logger


/**
 * @author Игорь Гулькин 25.04.2018
 *         <p>
 *         Класс HistoryRecyclerViewAdapter является той самой
 *         шишкой пользовательского интерфейса для работы с историями.
 */

class HistoryRecyclerViewAdapter : RecyclerView.Adapter<HistoryViewHolder>() {


    companion object {
        private val log = Logger.getLogger(HistoryRecyclerViewAdapter::class.java.name)
    }

    /**
     * 1.) THIS - провайдер менеджеров историй;
     *
     *
     * 2.) parentActivity - окно историй;
     *
     *
     * 3.) adapterMode - режим работы адаптера со списоком
     * в RecyclerView;
     *
     *
     * 4.) historyViewHolderList - список графических
     * объектов-привязок к историям (холдеров);
     *
     *
     * 5.) historyManagerHolderMap - карта, которая
     * хранит в себе ключ -> значение: холдер -> история;
     *
     *
     * 6.) HistoryActionConfigurator - настройщик адаптера,
     * позволяет динамически устанавливать, какие действия
     * и с какими историями должен работать адаптер.
     */

    private val historyViewHolderList: MutableList<HistoryViewHolder> = ArrayList()

    private val historyVsHolderMap: MutableMap<History, HistoryViewHolder> = HashMap()

    private val actionConfigurator = HistoryActionConfigurator()

    /**
     * onCreateViewHolder(final @NonNull ViewGroup parent, final int viewType)
     * вызывается, когда RecyclerView нуждается в новом ViewHolder заданном типе
     * для представления элемента.
     *
     * @param parent   - это специальное представление,
     * которое может содержать другие представления;
     * @param viewType - это альтернатива enum, только static и int;
     * @return объект-привзяку к элементу.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        //Достаем View GUI истории из XML:
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_history_item, parent, false)
        //Одеваем на холдер "вьюшку":
        val holder = HistoryViewHolder(view, this)
        //Толкаем в лист созданный холдер:
        this.historyViewHolderList.add(holder)
        return holder
    }

    /**
     * onBindViewHolder(final @NonNull HistoryViewHolder holder, final int currentPosition)
     * вызывается RecyclerView для отображения данных в указанной позиции.
     *
     * @param holder   - это объект-привязка к элементу;
     * @param position - это номер элементы.
     */

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentHistoryManager = HistoryManagerProvider.THIS!!.current
        val history = currentHistoryManager!!.getHistory(position)
        //Установили параметры из истории:
        holder.textViewHeadline?.text = history.headline
        holder.textViewDescription?.text = history.pullDescription()
        //Затем обязательно привязываем к historyHolder позицию:
        holder.currentPosition = position
        //Связываем историю и холдер через map:
        this.historyVsHolderMap[history] = holder
        //Потом устанавливаем "галочку" для элемента через historyManager,
        //который как раз и хранит в себе помеченные элементы:
        this.setHistoryTickVisibility(history, holder)
        //Устанавливаем переименовываемую историю:
        this.setHistoryTextEditor(history, holder)
        //Устанавливаем GUI для синхронизированной / несинхронизированной истории:
        val repositoryButton = holder.historyRepositoryImageView
        val labelID: Int
        labelID = if (HistoryManagerProvider.THIS!!.isSynchronizedHistory(history)) {
            currentHistoryManager.getSynchronizedLabel()
        } else {
            currentHistoryManager.getLabel()
        }
        repositoryButton?.setImageResource(labelID)
        when (HistoryListActivity.THIS?.activityMode) {
            HistoryListActivityMode.BROWSING -> HistoryViewUtils.showLabelAndHideTick(holder)
            HistoryListActivityMode.SELECTING -> HistoryViewUtils.hideHistoryLabelAndShowCell(holder)
            HistoryListActivityMode.RENAMING -> HistoryViewUtils.showLabelAndHideTick(holder)
            else -> {
            }
        }
    }

    /**
     * setHistoryTickVisibility(final History history, final HistoryViewHolder holder)
     * устанавливает галочку над холдером.
     *
     * @param history - это история;
     * @param holder  - это холдер.
     */

    private fun setHistoryTickVisibility(history: History, holder: HistoryViewHolder) {
        val historyManager = HistoryManagerProvider.THIS!!.current
        val imageViewTick = holder.imageViewTick
        val isSelectedHistory = historyManager!!.isSelectedHistory(history)
        val visibility = if (isSelectedHistory) View.VISIBLE else View.INVISIBLE
        imageViewTick?.visibility = visibility
    }

    /**
     * setHistoryTickVisibility(final History history, final HistoryViewHolder holder)
     * устанавливает переименовываемую историю.
     *
     * @param history - это история;
     * @param holder  - это холдер.
     */

    private fun setHistoryTextEditor(history: History, holder: HistoryViewHolder) {
        val editText = holder.historyHeadlineTextEditor
        val headlineText = holder.textViewHeadline
        if (HistoryListActivity.THIS?.activityMode == HistoryListActivityMode.RENAMING) {
            val historyManager = HistoryManagerProvider.THIS!!.current
            val hasOneSelectedHistory = historyManager!!.hasOneSelectedHistory()
            val isSelectedHistory = historyManager.isSelectedHistory(history)
            val isThisHistory = hasOneSelectedHistory && isSelectedHistory
            val editTextVisibility = if (isThisHistory) View.VISIBLE else View.INVISIBLE
            val headlineTextInvisibility = if (!isThisHistory) View.VISIBLE else View.INVISIBLE
            editText?.visibility = editTextVisibility
            headlineText?.visibility = headlineTextInvisibility
        }
    }

    /**
     * getItemCount() считает количество историй.
     *
     * @return число элементов в списке.
     */

    override fun getItemCount(): Int = HistoryManagerProvider.THIS!!.current!!
            .getNumberOfHistories()

    /**
     * removeSelectedHistories() удаляет историю из списка.
     */

    fun removeSelectedHistories() {
        //Истории которые мы не видим через RecyclerView просто удаляем:
        HistoryManagerProvider.THIS!!.removeSelectedHistories()
        this.notifyDataSetChanged()
        HistoryListActivity.THIS?.refresh()
        HistoryListActivity.THIS?.popupCancelButton?.showPopupCancelButton(HistoryListActivity
                .CancelActionType.CANCEL_REMOVE)
        this.startRemovedHistoriesClearingCountdown()
    }

    private fun startRemovedHistoriesClearingCountdown() {
        val millis: Long = 3000
        Handler().postDelayed({
            val popupCancelButton = HistoryListActivity.THIS?.popupCancelButton
            popupCancelButton?.hidePopupCancelButton()
            HistoryManagerProvider.THIS?.clearRemovedHistories()
        }, millis)
    }

    /**
     * renameSelectedHistory() запускает процесс редактирования
     * заголовка выбранной истории.
     */

    fun renameSelectedHistory() {
        HistoryListActivity.THIS?.activityMode = HistoryListActivityMode.RENAMING
        val history = HistoryManagerProvider.THIS!!.current!!.selectedHistory
        val holder = this.historyVsHolderMap[history]
        holder?.textViewHeadline?.visibility = View.INVISIBLE
        holder?.historyHeadlineTextEditor?.visibility = View.VISIBLE
        HistoryViewUtils.showAllLabelsAndHideAllTicks(this.historyViewHolderList)
    }

    /**
     * selectAllHistories() добавляет все выбранные истории.
     */

    fun selectAllHistories() {
        HistoryViewUtils.hideAllEditButtonsAndSelectAllTicks(this.historyViewHolderList)
        HistoryManagerProvider.THIS?.selectAllHistories()
        HistoryListActivity.THIS?.editBar?.setSelectAllHistoriesText()
    }

    /**
     * deselectAllHistories() удаляет все выбранные истории.
     */

    fun deselectAllHistories() {
        HistoryViewUtils.hideAllEditButtonsAndShowAllEmptyTicks(this.historyViewHolderList)
        HistoryManagerProvider.THIS!!.deselectAllHistories()
    }

    /**
     * handleOnHistoryClick() обрабатывает событие клика на историю.
     * Существуют 3 режима обработки события:
     *
     *
     * 1.) BROWSING: переходит на окно конкретной истории;
     * 2.) SELECTING: выбирает историю или сбрасывает;
     * 3.) RENAMING: сбрасывает редактируемый заголовок истории.
     */

    fun handleOnHistoryClick(imageViewTick: ImageView, currentPosition: Int) {
        val historyManager = HistoryManagerProvider.THIS!!.current
        when (HistoryListActivity.THIS?.activityMode) {
            HistoryListActivityMode.BROWSING -> {
                //Идем в историю --->
                HistoryListActivity.THIS!!.goToCurrentHistory(historyManager!!
                        .getHistory(currentPosition))
            }
            HistoryListActivityMode.SELECTING -> {
                //Если история не выбрана, то выбираем и ставим галочку:
                if (imageViewTick.visibility == View.INVISIBLE) {
                    HistoryManagerProvider.THIS!!.selectHistory(currentPosition)
                    imageViewTick.visibility = View.VISIBLE
                } else {
                    //Снимаем галочку...
                    HistoryManagerProvider.THIS!!.deselectHistory(currentPosition)
                    imageViewTick.visibility = View.INVISIBLE
                }
                HistoryManagerProvider.THIS!!.current!!.logSelectedHistories()
            }
            HistoryListActivityMode.RENAMING -> {
                //Устанавливаем заголовок, который был до переименовывания:
                val history = historyManager!!.selectedHistory
                this.setNewHeadlineInHistory(history!!.headline)
            }
            else -> {
            }
        }
    }

    /**
     * changeMode() переключает режим адаптера.
     */

    //Пока не используется:
    fun changeMode(imageViewTick: ImageView, currentPosition: Int) {
        when (HistoryListActivity.THIS?.activityMode) {
            HistoryListActivityMode.BROWSING -> {
                this.switchFromBrowsingToSelectingMode()
                HistoryManagerProvider.THIS!!.selectHistory(currentPosition)
                imageViewTick.visibility = View.VISIBLE
            }
            HistoryListActivityMode.SELECTING ->
                //Переходим в режим просмотра:
                this.switchFromSelectingToBrowsingMode()
            HistoryListActivityMode.RENAMING ->
                //Устанавливаем заголовок, который был до переименовывания:
                this.resetHistoryHeadline()
            else -> {
            }
        }
    }

    /**
     * switchFromBrowsingToSelectingMode()
     * переключает адаптер с режима просмотра в
     * режим выбора.
     */

    fun switchFromBrowsingToSelectingMode() {
        HistoryListActivity.THIS?.activityMode = HistoryListActivityMode.SELECTING
        HistoryViewUtils.hideAllHistoryLabelsAndShowAllCells(this.historyViewHolderList)
        log.info("${HistoryManagerProvider.THIS?.current?.selectedHistory}")
    }

    /**
     * switchFromSelectingToBrowsingMode()
     * переключает апаптер с режима выбоа в режим
     * просмотра.
     */

    fun switchFromSelectingToBrowsingMode() {
        HistoryListActivity.THIS?.activityMode = HistoryListActivityMode.BROWSING
        HistoryManagerProvider.THIS!!.deselectAllHistories()
        HistoryViewUtils.showAllLabelsAndHideAllTicks(this.historyViewHolderList)
    }

    /**
     * resetHistoryHeadline()
     *
     * Сбрасывает заголовок переименовываемой истории.
     */

    private fun resetHistoryHeadline() {
        val history = HistoryManagerProvider.THIS!!.current!!.selectedHistory
        val holder = this.historyVsHolderMap[history]
        holder?.historyHeadlineTextEditor?.setHint(R.string.put_new_history_name)
        holder?.historyHeadlineTextEditor?.setText(R.string.put_new_history_name)
        this.setNewHeadlineInHistory(history!!.headline)
    }

    /**
     * completeHistoryHeaderRenaming(final int actionID)
     * завершает процесс переименовывания истории.
     *
     * @param actionID указывает нажатую кнопку;
     */

    fun completeHistoryHeaderRenaming(actionID: Int, historyHeadlineTextEditor: EditText) {
        if (actionID == EditorInfo.IME_ACTION_DONE) {
            //Вытягиваем новый текст:
            val newText = historyHeadlineTextEditor.text.toString()
            //Тянем выбранную историю:
            val selectedHistory = HistoryManagerProvider.THIS!!.current!!.selectedHistory
            //Перед тем как перезаписывать, нужно проверить валидность текста:
            val isValidVsMsg = checkHistoryName(selectedHistory, newText)
            val isValidNewName = isValidVsMsg.first
            val message = isValidVsMsg.second
            if (isValidNewName) {
                //Можно переименовывать:
                log.info("SUCCESS OF RENAMING!!!")
                Toasty.success(HistoryListActivity.THIS!!, message).show()
                this.setNewHeadlineInHistory(newText)
            } else {
                Toasty.error(HistoryListActivity.THIS!!, message).show()
            }
        }
        log.info("ENTER CLICKED!")
    }

    /**
     * checkHistoryName(final History renamedHistory, final String newHeadline)
     * Проверяет, можно ли устанавливать новый заголовок в данную историю.
     *
     * @param renamedHistory - данная история;
     * @param newHeadline    - новый заголовок.
     * @return пару - истина или ложь и сообщение для Toast.
     */

    private fun checkHistoryName(renamedHistory: History?, newHeadline: String): Pair<Boolean, String> {
        val histories = HistoryManagerProvider.THIS!!.current!!.histories
        //Пустой текст нельзя:
        val isEmpty = newHeadline == ""
        if (isEmpty) {
            return Pair(false, "Заголовок истории не должен быть пустым")
        }
        //Заголовок не должен повторяться с другими заголовками историй:
        for (history in histories) {
            val isDifferentHistories = renamedHistory != history
            val matchesOther = newHeadline.trim { it <= ' ' } == history.headline!!.trim { it <= ' ' }
            if (isDifferentHistories && matchesOther) {
                return Pair(false, "Такое имя уже существует")
            }
        }
        return Pair(true, "Переименовано")
    }

    /**
     * setNewHeadlineInHistory(final String newText)
     * устанавливает новый заголовок в выбранную историю и
     * устанавливает адаптер в режим редактирования.
     *
     * @param newText - новый заголовок.
     */

    private fun setNewHeadlineInHistory(newText: String?) {
        val historyManager = HistoryManagerProvider.THIS!!.current
        val selectedHistory = historyManager!!.selectedHistory
        //Достали холдер, который привязан к соотвествующей истории:
        val holder = this.historyVsHolderMap[selectedHistory]
        //Меняем значения в истории:
        HistoryManagerProvider.THIS!!.setHeadlineForSelectedHistory(newText!!)
        val headline = holder?.textViewHeadline
        headline?.text = newText
        headline?.visibility = View.VISIBLE
        holder?.historyHeadlineTextEditor?.visibility = View.INVISIBLE
        //Теперь убираем клавиатуру с экрана:
        HistoryListActivity.THIS?.keyBoardSupplier?.hideKeyBoard()
        //Прячем все кнопки "Редактировать":
        HistoryViewUtils.hideAllHistoryLabelsAndShowAllCells(this.historyViewHolderList)
        //Над переименованной кнопкой ставим галочку:
        holder?.imageViewTick?.visibility = View.VISIBLE
        //Переходим в режим выбора:
        log.info("Переименовано!!!")
        HistoryListActivity.THIS?.activityMode = HistoryListActivityMode.SELECTING
    }

    /**
     * synchronize() синхронизирует данную историю
     * между облачным и локальным хранилищем.
     *
     * @param currentPosition позиция истории в списке.
     */

    fun synchronize(currentPosition: Int) {
        val isSynchronized = HistoryManagerProvider.THIS!!.synchronize(currentPosition)
        this.notifySynchronizedData(isSynchronized)
    }

    /**
     * synchronizeAll() синхронизирует все истории
     * между облачным и локальным хранилищем.
     */

    fun synchronizeAll() {
        //Если все истории синхронизированы, то нет кнопки "Синхронизировать":
        val isAllSynchronized = HistoryManagerProvider.THIS!!.isAllSynchronized()
        if (isAllSynchronized) {
            Toasty.info(HistoryListActivity.THIS!!, "Все истории уже были синхронизированы"
                    , Toast.LENGTH_LONG).show()
        } else {
            val isSuccessfulSync = HistoryManagerProvider.THIS!!.synchronizeAll()
            this.notifySynchronizedData(isSuccessfulSync)
        }
    }

    /**
     * synchronize() отображает синхронизацию
     * в RecyclerView, если произошла синхронизация.
     *
     * @param isSynchronized указать произошла ли
     * синхронизация или нет.
     */

    private fun notifySynchronizedData(isSynchronized: Boolean) {
        if (isSynchronized) {
            this.notifyDataSetChanged()
            //Показываем сообщение на экране:
            val from = HistoryManagerProvider.THIS!!.current!!.getRepositoryHeadlinePostfix()
            val to = HistoryManagerProvider.THIS!!.opposite!!
                    .getRepositoryHeadlinePostfix()
            this.showSyncToast(HistoryViewUtils.convertToPluralWord(from), HistoryViewUtils.convertToPluralWord(to))
        }
    }

    fun synchronizeSelected() {
        HistoryManagerProvider.THIS?.synchronizeSelected()
        this.notifySynchronizedData(true)
    }

    /**
     * showSyncToast(final String from, String to)
     * показывает сообщение на экране об
     * успешной синхронизации.
     *
     *
     * Параметрами являются ключевые слова
     * из метода getRepositoryHeadlinePostfix()
     * абстрактного класса [HistoryManager].
     * Имплементации:
     * [CloudHistoryManager] и [LocalHistoryManager].
     *
     * @param from - из какого репозитория берутся истории;
     * @param to   - в какой репозиторий они синхронизируются.
     */

    private fun showSyncToast(from: String, to: String) {
        val message = (from + " истории успешно загрузились в "
                + to.toLowerCase() + " данные")
        Toasty.success(HistoryListActivity.THIS!!, message, Toast.LENGTH_LONG).show()
    }

    fun sortHistoryListByAlphabet() {
        HistoryManagerProvider.THIS!!.sortByAlphabet()
        this.notifyDataSetChanged()
    }

    fun sortHistoryListByDate() {
        HistoryManagerProvider.THIS!!.sortByDate()
        this.notifyDataSetChanged()
    }

    /**
     * makeAction() неявно выполняет действие над историями,
     * которое было установлено в соответствии с конфигуациями для адаптера.
     */


    fun makeAction() {
        //Совершили действие:
        this.actionConfigurator.currentAction.apply()
        //Обнулили конфигурации:
        this.resetConfiguration()
    }

    /**
     * resetConfiguration() обнуляет установленную конфигурацию
     */

    private fun resetConfiguration() {
        this.actionConfigurator.currentAction = this.actionConfigurator.actionEnumMap[ActionType
                .NONE]!!
        this.actionConfigurator.currentFilter = this.actionConfigurator.filterEnumMap[FilterType
                .NONE]!!
        HistoryManagerProvider.THIS!!.synchronizedMode = false
    }

    /**
     * setCurrentAction(final ActionType actionType) устанавливает
     * новое действие для адаптера, которое совершиться в методе
     * makeAction().
     *
     * @param actionType - тип дейстия.
     */

    fun setCurrentAction(actionType: ActionType) {
        val action = this.actionConfigurator.actionEnumMap[actionType]
        this.actionConfigurator.currentAction = action!!
    }

    /**
     * setCurrentFilter(final FilterType filterType) устанавливает
     * фильтр: над какими историями нужно совершить действие.
     *
     * @param filterType - тип выбора историй.
     */

    fun setCurrentFilter(filterType: FilterType) {
        val filter = this.actionConfigurator.filterEnumMap[filterType]
        this.actionConfigurator.currentFilter = filter!!
    }

    /**
     * setEnableFilter() включает или выключает фильтр отбора историй.
     *
     * @param enable истина / ложь.
     */

    fun setEnableFilter(enable: Boolean) {
        this.actionConfigurator.currentFilter.setEnable(enable)
    }

    /**
     * Для адаптера существует HistoryActionConfigurator,
     * который позволяет вручную настраивать адаптер.
     * По сути HistoryActionConfigurator говорит адаптеру,
     * что он должен сделать и по какому критерию
     * выбрать истории, над которыми нужно провести
     * данную операцию.
     *
     *
     * Конечно, у адаптера методы public, и в любом
     * случае можно использовать его методы на прямую,
     * но для более гибких операции, например, с
     * всплывающими окнами, необходимо динамически
     * встраивать логику прямо в обработчики
     * событий всплывающего окна. Это достигается
     * за счет SAM интерфейсов.
     */

    private inner class HistoryActionConfigurator internal constructor() {

        internal var currentAction: HistoryAction

        internal var currentFilter: HistoryFilter

        /**
         * EnumMaps хранят в себе операции выбора
         * истории и действия по типу соответственно.
         */

        internal val filterEnumMap: EnumMap<FilterType, HistoryFilter>

        internal val actionEnumMap: EnumMap<ActionType, HistoryAction>

        init {
            //Настраиваем действия по типу:
            this.actionEnumMap = object : EnumMap<ActionType, HistoryAction>(ActionType::class.java) {
                init {
                    this[ActionType.REMOVE] = HistoryAction { removeSelectedHistories() }
                    this[ActionType.NONE] = HistoryAction { }
                }
            }
            //Настраиваем критерий-выбор по типу:
            this.filterEnumMap = object : EnumMap<FilterType, HistoryFilter>(FilterType::class.java) {
                init {
                    this[FilterType.SYNCHRONIZED] = HistoryFilter {
                        HistoryManagerProvider.THIS?.synchronizedMode = it
                    }
                    this[FilterType.NONE] = HistoryFilter { }
                }
            }
            //Устанавливаем исходное положение:
            this.currentAction = this.actionEnumMap[ActionType.NONE]!!
            this.currentFilter = this.filterEnumMap[FilterType.NONE]!!
        }
    }
}