package com.historyManagement.historyManagement.implementations;

import android.annotation.SuppressLint;

import com.annotations.NeedImplementation;
import com.example.starkre.sleepAlertHistory.R;
import com.historyManagement.history.historyData.History;
import com.historyManagement.historyManagement.HistoryManager;

import java.util.List;

/**
 * @author Игорь Гулькин on 03.05.2018.
 *         <p>
 *         Класс LocalHistoryManager реализует класс
 *         {@link HistoryManager} основным методом
 *         uploadData() загрузки данныых в локальное
 *         хранилище. Дополнительно реалиует GUI:
 *         <p>
 *         а.) getLabel() - иконка истории;
 *         <p>
 *         б.) getRepositoryHeadlinePostfix() -
 *         добавочный постфикс заголовка истории
 *         при синхронизации.
 */

public final class LocalHistoryManager extends HistoryManager {

    /**
     * getLabel()
     *
     * @return ID иконки из ресурсов.
     */

    @SuppressLint("WrongViewCast")
    @Override
    public final int getLabel() {
        return R.drawable.history_local_repository_label_image_view;
    }

    @Override
    public final int getColor() {
        return R.color.night_theme_blue_history;
    }

    /**
     * getRepositoryHeadlinePostfix()
     *
     * @return постфикс для заголовка.
     */

    @Override
    public final String getRepositoryHeadlinePostfix() {
        return " (Локальный)";
    }

    /**
     * uploadData() выгружает данные в локальное хранилище.
     */

    @NeedImplementation
    @Override
    public final void uploadData(final History history) {

    }

    @NeedImplementation
    @Override
    public final void uploadData(final History... histories) {
        //Предположения...
        // for (final History history : histories){
        //    this.uploadData(history);
        // }
    }

    @NeedImplementation
    @Override
    public final void uploadData(final List<History> historyList) {
        //Предположения...
        // for (final History history : historyList){
        //    this.uploadData(history);
        // }
    }
}