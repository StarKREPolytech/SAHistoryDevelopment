package com.historyManagement.history.historyData;

import com.annotations.Temporary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author Игорь Гулькин 11.05.2018.
 *         <p>
 *         DataGenerator имитирует сеанс поездки.
 *         Генерирует посылки, полученные с устройства.
 */

@Temporary
final class DataGenerator {

    final List<InputData> generateInputData() {
        final List<InputData> dataList = new ArrayList<>();
        final Random random = new Random();
        for (int i = 0; i < random.nextInt(100000); i++) {
            final double level = random.nextInt(100);
            final InputData inputData = new InputData(level, i, random.nextBoolean());
            dataList.add(inputData);
        }
        return dataList;
    }
}