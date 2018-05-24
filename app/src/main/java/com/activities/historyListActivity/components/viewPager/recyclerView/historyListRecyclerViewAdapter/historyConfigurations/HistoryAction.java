package com.activities.historyListActivity.components.viewPager.recyclerView.historyListRecyclerViewAdapter.historyConfigurations;

/**
 * @author Игорь Гулькин 09.05.2018.
 *
 * Интерфейс HistoryAction является
 * оберткой void метода из адаптера,
 * которая используется для вызова
 * того или иного действия над историями.
 */

public interface HistoryAction {

    void apply();
}