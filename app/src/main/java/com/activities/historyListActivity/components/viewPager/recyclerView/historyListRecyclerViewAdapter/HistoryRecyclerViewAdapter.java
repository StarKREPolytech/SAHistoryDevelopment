package com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.historyListActivity.components.viewPager.fragment.RepositoryFragment;
import com.example.starkre.sleepAlertHistory.R;
import com.activities.historyListActivity.HistoryListActivity;
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.ActionType;
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.HistoryAction;
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.FilterType;
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations.HistoryFilter;
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.mode.AdapterMode;
import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.viewHolder.HistoryViewHolder;
import com.historyManagement.history.historyData.History;
import com.historyManagement.historyManagment.HistoryManager;
import com.historyManagement.historyManagment.implementations.CloudHistoryManager;
import com.historyManagement.historyManagment.implementations.LocalHistoryManager;
import com.historyManagement.provider.HistoryManagerProvider;
import com.historyManagement.utilities.HistoryViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.mode.AdapterMode.BROWSING;
import static com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.mode.AdapterMode.RENAMING;
import static com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.mode.AdapterMode.SELECTING;

/**
 * @author Игорь Гулькин 25.04.2018
 *         <p>
 *         Класс HistoryRecyclerViewAdapter является той самой
 *         шишкой пользовательского интерфейса для работы с историями.
 */

@Slf4j
public final class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    /**
     * Изначально адаптер находится в режиме просмотра историй.
     */

    private static final AdapterMode START_ADAPTER_MODE = BROWSING;

    /**
     * 1.) THIS - провайдер менеджеров историй;
     * <p>
     * 2.) parentActivity - окно историй;
     * <p>
     * 3.) adapterMode - режим работы адаптера со списоком
     * в RecyclerView;
     * <p>
     * 4.) historyViewHolderList - список графических
     * объектов-привязок к историям (холдеров);
     * <p>
     * 5.) historyManagerHolderMap - карта, которая
     * хранит в себе ключ -> значение: холдер -> история;
     * <p>
     * 6.) HistoryActionConfigurator - настройщик адаптера,
     * позволяет динамически устанавливать, какие действия
     * и с какими историями должен работать адаптер.
     */

    @Getter
    private HistoryListActivity parentActivity;

    @Getter
    @Setter
    private AdapterMode adapterMode;

    @Getter
    private final List<HistoryViewHolder> historyViewHolderList;

    @Getter
    private final Map<History, HistoryViewHolder> historyVsHolderMap;

    private final HistoryActionConfigurator actionConfigurator;

    public HistoryRecyclerViewAdapter(final HistoryListActivity parentActivity) {
        //Устанавливаем родительское окно:
        this.parentActivity = parentActivity;
        //Инициализируем конфигурацию для работы с историями:
        this.actionConfigurator = new HistoryActionConfigurator();
        //Создаем контейнеры:
        this.historyViewHolderList = new ArrayList<>();
        this.historyVsHolderMap = new HashMap<>();
        //Устанавливаем адаптер в начальный режим:
        this.adapterMode = START_ADAPTER_MODE;
    }

    /**
     * onCreateViewHolder(final @NonNull ViewGroup parent, final int viewType)
     * вызывается, когда RecyclerView нуждается в новом ViewHolder заданном типе
     * для представления элемента.
     *
     * @param parent   - это специальное представление,
     *                 которое может содержать другие представления;
     * @param viewType - это альтернатива enum, только static и int;
     * @return объект-привзяку к элементу.
     */

    @NonNull
    @Override
    public final HistoryViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        //Достаем View GUI истории из XML:
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_history_item, parent, false);
        //Одеваем на холдер "вьюшку":
        final HistoryViewHolder holder = new HistoryViewHolder(view, this);
        //Толкаем в лист созданный холдер:
        this.historyViewHolderList.add(holder);
        return holder;
    }

    /**
     * onBindViewHolder(final @NonNull HistoryViewHolder holder, final int currentPosition)
     * вызывается RecyclerView для отображения данных в указанной позиции.
     *
     * @param holder   - это объект-привязка к элементу;
     * @param position - это номер элементы.
     */

    @Override
    public final void onBindViewHolder(final HistoryViewHolder holder, final int position) {
        final HistoryManager currentHistoryManager = HistoryManagerProvider.THIS.get();
        final History history = currentHistoryManager.getHistory(position);
        //Установили параметры из истории:
        holder.getTextViewHeadline().setText(history.getHeadline());
        holder.getTextViewDescription().setText(history.pullDescription());
        //Затем обязательно привязываем к historyHolder позицию:
        holder.setCurrentPosition(position);
        //Связываем историю и холдер через map:
        this.historyVsHolderMap.put(history, holder);
        //Потом устанавливаем "галочку" для элемента через historyManager,
        //который как раз и хранит в себе помеченные элементы:
        this.setHistoryTick(history, holder);
        //Устанавливаем переименовываемую историю:
        this.setHistoryTextEditor(history, holder);
        //Устанавливаем GUI для синхронизированной / несинхронизированной истории:
        final ImageView repositoryButton = holder.getHistoryRepositoryImageView();
        final int labelID;
        final int colorID;
        if (HistoryManagerProvider.THIS.isSynchronizedHistory(history)) {
            labelID = currentHistoryManager.getSynchronizedLabel();
            colorID = currentHistoryManager.getSynchronizedColor();
        } else {
            labelID = currentHistoryManager.getLabel();
            colorID = currentHistoryManager.getColor();
        }
        repositoryButton.setImageResource(labelID);
        holder.getLabelImageView().setBackgroundResource(colorID);
        switch (this.adapterMode) {
            case BROWSING:
                HistoryViewUtils.showEditButtonAndHideTick(holder);
                break;
            case SELECTING:
                HistoryViewUtils.hideEditButtonAndShowTick(holder);
                break;
            case RENAMING:
                HistoryViewUtils.showEditButtonAndHideTick(holder);
        }
    }

    /**
     * setHistoryTick(final History history, final HistoryViewHolder holder)
     * устанавливает галочку над холдером.
     *
     * @param history - это история;
     * @param holder  - это холдер.
     */

    private void setHistoryTick(final History history, final HistoryViewHolder holder) {
        final HistoryManager historyManager = HistoryManagerProvider.THIS.get();
        final ImageView imageViewTick = holder.getImageViewTick();
        final boolean isSelectedHistory = historyManager.isSelectedHistory(history);
        final int visibility = isSelectedHistory ? View.VISIBLE : View.INVISIBLE;
        imageViewTick.setVisibility(visibility);
    }

    /**
     * setHistoryTick(final History history, final HistoryViewHolder holder)
     * устанавливает переименовываемую историю.
     *
     * @param history - это история;
     * @param holder  - это холдер.
     */

    private void setHistoryTextEditor(final History history, final HistoryViewHolder holder) {
        final EditText editText = holder.getHistoryHeadlineTextEditor();
        final TextView headText = holder.getTextViewHeadline();
        if (this.adapterMode == RENAMING) {
            final HistoryManager historyManager = HistoryManagerProvider.THIS.get();
            final boolean hasOneSelectedHistory = historyManager.hasOneSelectedHistory();
            final boolean isSelectedHistory = historyManager.isSelectedHistory(history);
            final boolean isThisHistory = hasOneSelectedHistory && isSelectedHistory;
            final int visibility = isThisHistory ? View.VISIBLE : View.INVISIBLE;
            final int invisibility = !isThisHistory ? View.VISIBLE : View.INVISIBLE;
            editText.setVisibility(visibility);
            headText.setVisibility(invisibility);
        }
    }

    /**
     * getItemCount() считает количество историй.
     *
     * @return число элементов в списке.
     */

    @Override
    public final int getItemCount() {
        return HistoryManagerProvider.THIS.get().getNumberOfHistories();
    }

    /**
     * removeSelectedHistories() удаляет историю из списка.
     */

    public final void removeSelectedHistories() {
        final HistoryManager historyManager = HistoryManagerProvider.THIS.get();
        final int selectedSize = historyManager.getSelectedHistories().size();
        //Истории которые мы не видим через RecyclerView просто удаляем:
        HistoryManagerProvider.THIS.removeSelectedHistories();
        this.notifyDataSetChanged();
        this.parentActivity.refreshScreen();
        Toasty.info(this.parentActivity, "Удалено: " + selectedSize).show();
    }

    /**
     * renameSelectedHistory() запускает процесс редактирования
     * заголовка выбранной истории.
     */

    public final void renameSelectedHistory() {
        this.adapterMode = RENAMING;
        final History history = HistoryManagerProvider.THIS.get().getSelectedHistory();
        final HistoryViewHolder holder = this.historyVsHolderMap.get(history);
        holder.getTextViewHeadline().setVisibility(View.INVISIBLE);
        holder.getHistoryHeadlineTextEditor().setVisibility(View.VISIBLE);
        HistoryViewUtils.showAllEditButtonsAndHideAllTicks(this.historyViewHolderList);
    }

    /**
     * selectAllHistories() добавляет все выбранные истории.
     */
//
//    public final void selectAllHistories() {
//        final HistoryBottomBar historyBottomBar
//                = RepositoryFragment.THIS.getHistoryBottomBar();
//        HistoryViewUtils.hideAllEditButtonsAndSelectAllTicks(this.historyViewHolderList);
//        HistoryManagerProvider.THIS.selectAllHistories();
//        historyBottomBar.setRenameHistoryButtonVisibility();
//    }

    /**
     * deselectAllHistories() удаляет все выбранные истории.
     */

    public final void deselectAllHistories() {
        HistoryViewUtils.hideAllEditButtonsAndShowAllEmptyTicks(this.historyViewHolderList);
        HistoryManagerProvider.THIS.deselectAllHistories();
    }

    /**
     * handleOnHistoryClick() обрабатывает событие клика на историю.
     * Существуют 3 режима обработки события:
     * <p>
     * 1.) BROWSING: переходит на окно конкретной истории;
     * 2.) SELECTING: выбирает историю или сбрасывает;
     * 3.) RENAMING: сбрасывает редактируемый заголовок истории.
     */

    public final void handleOnHistoryClick(final ImageView imageViewTick
            , final int currentPosition) {
        final HistoryManager historyManager = HistoryManagerProvider.THIS.get();
        switch (this.adapterMode) {
            case BROWSING:
                //Идем в историю --->
                HistoryListActivity.THIS.goToCurrentHistory(historyManager.getHistory(currentPosition));
                break;
            case SELECTING:
                //Если история не выбрана, то выбираем и ставим галочку.
                System.out.println("CURRENT POSITION: " + currentPosition);
                if (imageViewTick.getVisibility() == View.INVISIBLE) {
                    HistoryManagerProvider.THIS.selectHistory(currentPosition);
                    imageViewTick.setVisibility(View.VISIBLE);
                } else {
                    //Снимаем галочку...
                    HistoryManagerProvider.THIS.deselectHistory(currentPosition);
                    imageViewTick.setVisibility(View.INVISIBLE);
                }
                break;
            case RENAMING:
                //Устанавливаем заголовок, который был до переименовывания:
                final History history = historyManager.getSelectedHistory();
                this.setNewHeadlineInHistory(history.getHeadline());
        }
    }

    /**
     * changeMode() переключает режим адаптера.
     */

    public void changeMode(final ImageView imageViewTick, final int currentPosition) {
        switch (this.adapterMode) {
            case BROWSING:
                this.switchFromBrowsingToSelectingMode();
                HistoryManagerProvider.THIS.selectHistory(currentPosition);
                imageViewTick.setVisibility(View.VISIBLE);
                break;
            case SELECTING:
                //Переходим в режим просмотра:
                this.switchFromSelectingToBrowsingMode();
                break;
            case RENAMING:
                //Устанавливаем заголовок, который был до переименовывания:
                this.resetHistoryHeadline();

        }
    }

    /**
     * switchFromBrowsingToSelectingMode()
     * переключает адаптер с режима просмотра в
     * режим выбора.
     */

    public final void switchFromBrowsingToSelectingMode() {
//        final HistoryTopBar historyTopBar = this.parentActivity
//                .getHistoryTopBar();
//        final HistoryBottomBar historyBottomBar = RepositoryFragment.THIS.getHistoryBottomBar();
//        final HistoryNavigationFrame navFrame = historyTopBar.getNavigationFrame();
//        this.adapterMode = SELECTING;
        HistoryViewUtils.hideAllEditButtonsAndShowAllTicks(this.historyViewHolderList);
//        historyTopBar.getNavigationFrame().setCancelEditLabel();
//        historyBottomBar.getSelectAllButton().setText(R.string.history_select_all);
//        navFrame.close();
    }

    /**
     * switchFromSelectingToBrowsingMode()
     * переключает апаптер с режима выбоа в режим
     * просмотра.
     */

    public final void switchFromSelectingToBrowsingMode() {
//        final HistoryTopBar historyTopBar = this.parentActivity.getHistoryTopBar();
//        final HistoryNavigationFrame navFrame = historyTopBar.getNavigationFrame();
//        this.adapterMode = BROWSING;
        HistoryManagerProvider.THIS.deselectAllHistories();
        HistoryViewUtils.showAllEditButtonsAndHideAllTicks(this.historyViewHolderList);
//        navFrame.setEditLabel();
//        navFrame.close();
    }

    /**
     * resetHistoryHeadline()
     * <p>
     * Сбрасывает заголовок переименовываемой истории.
     */

    private void resetHistoryHeadline() {
        final History history = HistoryManagerProvider.THIS.get().getSelectedHistory();
        final HistoryViewHolder holder = this.historyVsHolderMap.get(history);
        holder.getHistoryHeadlineTextEditor().setHint(R.string.put_new_history_name);
        holder.getHistoryHeadlineTextEditor().setText(R.string.put_new_history_name);
        this.setNewHeadlineInHistory(history.getHeadline());
    }

    /**
     * completeHistoryHeaderRenaming(final int actionID)
     * завершает процесс переименовывания истории.
     *
     * @param actionID указывает нажатую кнопку;
     */

    public final void completeHistoryHeaderRenaming(final int actionID
            , final EditText historyHeadlineTextEditor) {
        if (actionID == EditorInfo.IME_ACTION_DONE) {
            //Вытягиваем новый текст:
            final String newText = historyHeadlineTextEditor.getText().toString();
            //Тянем выбранную историю:
            final History selectedHistory = HistoryManagerProvider.THIS.get().getSelectedHistory();
            //Перед тем как перезаписывать, нужно проверить валидность текста:
            final Pair<Boolean, String> isValidVsMsg = checkHistoryName(selectedHistory, newText);
            final boolean isValidNewName = isValidVsMsg.first;
            final String message = isValidVsMsg.second;
            if (isValidNewName) {
                //Можно переименовывать:
                Toasty.success(this.parentActivity, message).show();
                this.setNewHeadlineInHistory(newText);
            } else {
                Toasty.error(this.parentActivity, message).show();
            }
        }
        log.info("ENTER CLICKED!");
    }

    /**
     * checkHistoryName(final History renamedHistory, final String newHeadline)
     * Проверяет, можно ли устанавливать новый заголовок в данную историю.
     *
     * @param renamedHistory - данная история;
     * @param newHeadline    - новый заголовок.
     * @return пару - истина или ложь и сообщение для Toast.
     */

    private Pair<Boolean, String> checkHistoryName(final History renamedHistory, final String newHeadline) {
        final List<History> histories = HistoryManagerProvider.THIS.get().getHistories();
        //Пустой текст нельзя:
        final boolean isEmpty = newHeadline.equals("");
        if (isEmpty) {
            return new Pair<>(false, "Заголовок истории не должен быть пустым");
        }
        //Заголовок не должен повторяться с другими заголовками историй:
        for (final History history : histories) {
            final boolean isDifferentHistories = !renamedHistory.equals(history);
            final boolean matchesOther = newHeadline.trim().equals(history.getHeadline().trim());
            if (isDifferentHistories && matchesOther) {
                return new Pair<>(false, "Такое имя уже существует");
            }
        }
        return new Pair<>(true, "Переименовано");
    }

    /**
     * setNewHeadlineInHistory(final String newText)
     * устанавливает новый заголовок в выбранную историю и
     * устанавливает адаптер в режим редактирования.
     *
     * @param newText - новый заголовок.
     */

    private void setNewHeadlineInHistory(final String newText) {
        final HistoryManager historyManager = HistoryManagerProvider.THIS.get();
        final History selectedHistory = historyManager.getSelectedHistory();
        //Достали холдер, который привязан к соотвествующей истории:
        final HistoryViewHolder holder = this.historyVsHolderMap.get(selectedHistory);
        //Меняем значения в истории:
        HistoryManagerProvider.THIS.setHeadlineForSelectedHistory(newText);
        final TextView headline = holder.getTextViewHeadline();
        headline.setText(newText);
        headline.setVisibility(View.VISIBLE);
        holder.getHistoryHeadlineTextEditor().setVisibility(View.INVISIBLE);
        //Теперь убираем клавиатуру с экрана:
        final View view = this.parentActivity.getCurrentFocus();
        if (view != null) {
            final String inputMethodService = Context.INPUT_METHOD_SERVICE;
            final InputMethodManager inputMethodManager
                    = (InputMethodManager) this.parentActivity.getSystemService(inputMethodService);
            //InputMethodManager точно не null:
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //Прячем все кнопки "Редактировать":
            HistoryViewUtils.hideAllEditButtonsAndShowAllTicks(this.historyViewHolderList);
            //Над переименованной кнопкой ставим галочку:
            holder.getImageViewTick().setVisibility(View.VISIBLE);
            //Переходим в режим выбора:
            this.adapterMode = SELECTING;
        }
    }

    /**
     * synchronize() синхронизирует данную историю
     * между облачным и локальным хранилищем.
     *
     * @param currentPosition позиция истории в списке.
     */

    public final void synchronize(final int currentPosition) {
        final boolean isSynchronized = HistoryManagerProvider.THIS.synchronize(currentPosition);
        this.synchronize(isSynchronized);
    }

    /**
     * synchronizeAll() синхронизирует все истории
     * между облачным и локальным хранилищем.
     */

    public final void synchronizeAll() {
        //Если все истории синхронизированы, то нет кнопки "Синхронизировать":
        final boolean isAllSynchronized = HistoryManagerProvider.THIS.isAllSynchronized();
        if (isAllSynchronized) {
            Toasty.info(this.parentActivity, "Все истории уже были синхронизированы"
                    , Toast.LENGTH_LONG).show();
        } else {
            final boolean isSuccessfulSync = HistoryManagerProvider.THIS.synchronizeAll();
            this.synchronize(isSuccessfulSync);
        }
    }

    /**
     * synchronize() отображает синхронизацию
     * в RecyclerView, если произошла синхронизация.
     *
     * @param isSynchronized указать произошла ли
     *                       синхронизация или нет.
     */

    private void synchronize(final boolean isSynchronized) {
        if (isSynchronized) {
            this.notifyDataSetChanged();
            //Показываем сообщение на экране:
            final String from = HistoryManagerProvider.THIS.get().getRepositoryHeadlinePostfix();
            final String to = HistoryManagerProvider.THIS.getOpposite()
                    .getRepositoryHeadlinePostfix();
            this.showSyncToast(HistoryViewUtils.convertToPluralWord(from)
                    , HistoryViewUtils.convertToPluralWord(to));
        }
    }

    /**
     * showSyncToast(final String from, String to)
     * показывает сообщение на экране об
     * успешной синхронизации.
     * <p>
     * Параметрами являются ключевые слова
     * из метода getRepositoryHeadlinePostfix()
     * абстрактного класса {@link HistoryManager}.
     * Имплементации:
     * {@link CloudHistoryManager} и {@link LocalHistoryManager}.
     *
     * @param from - из какого репозитория берутся истории;
     * @param to   - в какой репозиторий они синхронизируются.
     */

    private void showSyncToast(final String from, String to) {
        final String message = from + " истории успешно загрузились в "
                + to.toLowerCase() + " данные";
        Toasty.success(this.parentActivity, message, Toast.LENGTH_LONG).show();
    }

    public final void sortHistoryListByAlphabet() {
        HistoryManagerProvider.THIS.sortByAlphabet();
        this.notifyDataSetChanged();
    }

    public final void sortHistoryListByDate(){
        HistoryManagerProvider.THIS.sortByDate();
        this.notifyDataSetChanged();
    }

    /**
     * makeAction() неявно выполняет действие над историями,
     * которое было установлено в соответствии с конфигуациями для адаптера.
     */

    public final void makeAction() {
        //Совершили действие:
        this.actionConfigurator.currentAction.apply();
        //Обнулили конфигурации:
        this.resetConfiguration();
    }

    /**
     * resetConfiguration() обнуляет установленную конфигурацию
     */

    private void resetConfiguration() {
        this.actionConfigurator.currentAction = this.actionConfigurator.actionEnumMap
                .get(ActionType.NONE);
        this.actionConfigurator.currentFilter = this.actionConfigurator.filterEnumMap
                .get(FilterType.NONE);
        HistoryManagerProvider.THIS.setSynchronizedMode(false);
    }

    /**
     * setCurrentAction(final ActionType actionType) устанавливает
     * новое действие для адаптера, которое совершиться в методе
     * makeAction().
     *
     * @param actionType - тип дейстия.
     */

    public final void setCurrentAction(final ActionType actionType) {
        final HistoryAction action = this.actionConfigurator.actionEnumMap.get(actionType);
        this.actionConfigurator.currentAction = action;
    }

    /**
     * setCurrentFilter(final FilterType filterType) устанавливает
     * фильтр: над какими историями нужно совершить действие.
     *
     * @param filterType - тип выбора историй.
     */

    public final void setCurrentFilter(final FilterType filterType) {
        final HistoryFilter filter = this.actionConfigurator.filterEnumMap.get(filterType);
        this.actionConfigurator.currentFilter = filter;
    }

    /**
     * setEnableFilter() включает или выключает фильтр отбора историй.
     *
     * @param enable истина / ложь.
     */

    public final void setEnableFilter(final boolean enable) {
        this.actionConfigurator.currentFilter.setEnable(enable);
    }

    /**
     * Для адаптера существует HistoryActionConfigurator,
     * который позволяет вручную настраивать адаптер.
     * По сути HistoryActionConfigurator говорит адаптеру,
     * что он должен сделать и по какому критерию
     * выбрать истории, над которыми нужно провести
     * данную операцию.
     * <p>
     * Конечно, у адаптера методы public, и в любом
     * случае можно использовать его методы на прямую,
     * но для более гибких операции, например, с
     * всплывающими окнами, необходимо динамически
     * встраивать логику прямо в обработчики
     * событий всплывающего окна. Это достигается
     * за счет SAM интерфейсов.
     */

    private final class HistoryActionConfigurator {

        private HistoryAction currentAction;

        private HistoryFilter currentFilter;

        /**
         * EnumMaps хранят в себе операции выбора
         * истории и действия по типу соответственно.
         */

        private final EnumMap<FilterType, HistoryFilter> filterEnumMap;

        private final EnumMap<ActionType, HistoryAction> actionEnumMap;

        private HistoryActionConfigurator() {
            //Настраиваем действия по типу:
            this.actionEnumMap = new EnumMap<ActionType, HistoryAction>(ActionType.class) {{
                this.put(ActionType.REMOVE, HistoryRecyclerViewAdapter.this::removeSelectedHistories);
                this.put(ActionType.NONE, () -> {
                });
            }};
            //Настраиваем критерий-выбор по типу:
            this.filterEnumMap = new EnumMap<FilterType, HistoryFilter>(FilterType.class) {{
                this.put(FilterType.SYNCHRONIZED, HistoryManagerProvider.THIS::setSynchronizedMode);
                this.put(FilterType.NONE, enable -> {
                });
            }};
            //Устанавливаем исходное положение:
            this.currentAction = this.actionEnumMap.get(ActionType.NONE);
            this.currentFilter = this.filterEnumMap.get(FilterType.NONE);
        }
    }
}