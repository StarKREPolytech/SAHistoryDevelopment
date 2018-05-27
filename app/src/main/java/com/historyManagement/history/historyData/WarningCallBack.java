package com.historyManagement.history.historyData;

import com.annotations.NotFinal;

import java.util.logging.Logger;

/**
 * @author Игорь Гулькин 11.05.2018.
 * <p>
 * Удобный интерфейс, если Вы хотите
 * обрабатывать события WarningAnalyser
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

    @NotFinal
    class EmptyWarningCallBack implements WarningCallBack {

        private static final Logger log = Logger.getLogger(EmptyWarningCallBack.class.getName());

        @NotFinal
        @Override
        public void onHighLevel(final InputData inputData) {
            log.info("Stub");
        }

        @NotFinal
        @Override
        public void onCriticalLevel(final InputData inputData) {
            log.info("Stub");
        }

        @NotFinal
        @Override
        public void onAlarm(final InputData inputData) {
            log.info("Stub");
        }
    }
}