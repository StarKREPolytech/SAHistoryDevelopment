package com.historyManagement.historyManagment.implementations;

import android.annotation.SuppressLint;

import com.example.starkre.sleepAlertHistory.R;
import com.annotations.NeedImplementation;
import com.historyManagement.history.historyData.History;
import com.historyManagement.historyManagment.HistoryManager;

import java.util.List;

/**
 * @author Игорь Гулькин on 03.05.2018.
 *
 * Класс CloudHistoryManager реализует класс
 * {@link HistoryManager} основным методом
 * uploadData() загрузки данныых в облако.
 * Дополнительно реалиует GUI:
 *
 * а.) getLabel() - иконка истории;
 *
 * б.) getRepositoryHeadlinePostfix() -
 * добавочный постфикс заголовка истории
 * при синхронизации.
 */

public final class CloudHistoryManager extends HistoryManager {

    /**
     * getLabel()
     *
     * @return ID иконки из ресурсов.
     */

    @SuppressLint("WrongViewCast")
    @Override
    public final int getLabel() {
        return R.drawable.cloud_history_button;
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
        return " (Облачный)";
    }

    /**
     * uploadData() выгружает данные в облако.
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