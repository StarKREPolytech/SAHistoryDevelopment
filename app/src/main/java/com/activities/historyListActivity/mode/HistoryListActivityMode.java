package com.activities.historyListActivity.mode;

/**
 * @author Игорь Гулькин 27.04.2018
 *
 * Крохотный enum, определяющий в каком режиме работает HistoryListActivity с историями.
 * 1.) BROWSING - просмотр историй;
 * 2.) SELECTING - выбор историй;
 * 3.) RENAMING - переименовывание истории;
 * 4.) SHOW_OPTIONS - общие действия с историями.
 */

public enum HistoryListActivityMode {
    BROWSING, SELECTING, RENAMING, SHOW_OPTIONS
}