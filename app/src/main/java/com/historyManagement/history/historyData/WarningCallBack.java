package com.historyManagement.history.historyData;

import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Игорь Гулькин 11.05.2018.
 *         <p>
 *         Удобный интерфейс, если Вы хотите
 *         обрабатывать события WarningAnalyser
 */

public interface WarningCallBack {

    void onHighLevel(final InputData inputData);

    void onCriticalLevel(final InputData inputData);

    void onAlarm(final InputData inputData);

    /**
     * Прямо внутри интерфейса развернут статический класс EmptyWarningCallBack,
     * который не обрабатывает события WarningAnalyser. Если Вы не хотите реализовывать
     * весь интерфейс WarningCallBack, а нужно, например, один, то Вы можете
     * унаследоваться от класса EmptyWarningCallBack и передавить выбранный метод.
     */

    @Slf4j
    @NonFinal
    class EmptyWarningCallBack implements WarningCallBack {

        @NonFinal
        @Override
        public void onHighLevel(final InputData inputData) {
            log.info("Stub");
        }

        @NonFinal
        @Override
        public void onCriticalLevel(final InputData inputData) {
            log.info("Stub");
        }

        @NonFinal
        @Override
        public void onAlarm(final InputData inputData) {
            log.info("Stub");
        }
    }
}