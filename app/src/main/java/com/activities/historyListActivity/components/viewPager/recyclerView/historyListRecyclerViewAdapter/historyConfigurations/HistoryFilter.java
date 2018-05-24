package com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations;


/**
 * @author Игорь Гулькин 09.05.2018.
 *
 * Интерфейс FilterType является
 * оберткой над void методом выбора
 * историй по каким либо критериям.
 */

public interface HistoryFilter {

    void setEnable(final boolean enable);
}