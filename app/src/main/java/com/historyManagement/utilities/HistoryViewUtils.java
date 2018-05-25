package com.historyManagement.utilities;

import android.view.View;

import com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.viewHolder.HistoryViewHolder;

import java.util.List;

import lombok.experimental.UtilityClass;

/**
 * @author Игорь Гулькин 30.04.2018.
 *
 * Класс HistoryViewUtils - это крохотная утилитка,
 * которая позволяет избегать дублирование кода.
 */

@UtilityClass
public final class HistoryViewUtils {

    /**
     * setVisibility(final boolean condition, final View view)
     * устанавливает видимость/невидимость объекта по условию.
     *
     * @param condition - условие;
     * @param view - сам объект.
     */

    public static void setVisibility(final boolean condition, final View view) {
        final int visibility = condition ? View.VISIBLE : View.INVISIBLE;
        view.setVisibility(visibility);
    }

    public static void showAllEditButtonsAndHideAllTicks(
            final List<HistoryViewHolder> historyViewHolderList){
        for (final HistoryViewHolder holder : historyViewHolderList) {
            showEditButtonAndHideTick(holder);
        }
    }

    public static void showEditButtonAndHideTick(final HistoryViewHolder holder){
        holder.getHistoryRepositoryImageView().setVisibility(View.VISIBLE);
        holder.getImageViewTick().setVisibility(View.INVISIBLE);
        holder.getImageViewTickOff().setVisibility(View.INVISIBLE);
    }

    public static void hideAllHistoryLabelsAndShowAllCells(
            final List<HistoryViewHolder> historyViewHolderList){
        for (final HistoryViewHolder holder: historyViewHolderList){
            hideHistoryLabelAndShowCell(holder);
        }
    }

    public static void hideHistoryLabelAndShowCell(final HistoryViewHolder holder){
        holder.getLabelImageView().setVisibility(View.INVISIBLE);
        holder.getImageViewTickOff().setVisibility(View.VISIBLE);
    }

    public static void hideAllEditButtonsAndSelectAllTicks(
            final List<HistoryViewHolder> historyViewHolderList) {
        for (final HistoryViewHolder holder : historyViewHolderList){
            hideEditButtonAndSelectTick(holder);
        }
    }

    public static void hideEditButtonAndSelectTick(final HistoryViewHolder holder) {
        hideHistoryLabelAndShowCell(holder);
        holder.getImageViewTick().setVisibility(View.VISIBLE);
    }

    public static void hideAllEditButtonsAndShowAllEmptyTicks(
            final List<HistoryViewHolder> historyViewHolderList) {
        for (final HistoryViewHolder holder : historyViewHolderList){
            hideHistoryLabelAndShowCell(holder);
            holder.getImageViewTick().setVisibility(View.INVISIBLE);
        }
    }


    /**
     * convertToPluralWord(final String word)
     *
     * Преобразует прилагательное из ед. числа во
     * множественное и убирает скобочки.
     * <p>
     * Пример: (Локальный) -> Локальные
     *
     * @param word -  из метода
     *             getRepositoryHeadlinePostfix()
     *             абстрактного класса {@link com.historyManagement.historyManagement.HistoryManager}.
     * @return слово во мн. числе.
     */

    public static String convertToPluralWord(final String word) {
        return word.substring(2, word.length() - 2) + "e";
    }

    public static String convertToGenitiveWord(final String word){
        return word.substring(2, word.length() - 3) + "ого";
    }
}