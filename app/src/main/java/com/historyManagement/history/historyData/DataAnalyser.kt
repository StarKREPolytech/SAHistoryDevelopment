package com.historyManagement.history.historyData

import com.annotations.Temporary
import com.historyManagement.history.historyData.convinientTime.ConvenientTime
import lombok.experimental.NonFinal
import java.util.*
import java.util.function.Function
import java.util.logging.Logger

class DataAnalyser {

    companion object {
        @JvmField val  log = Logger.getLogger(DataAnalyser::class.java.name)
    }

    val inputDataList: MutableList<InputData> = ArrayList()

    /**
     * Процентные соотношения уровней усталости
     * хранят в себе не статические значения, а
     * значения в зависимости от установленной
     * конфигурации для DataAnalyser.
     * По умолчанию устанавливается настройка
     * на весь интервал поездки, но если
     * необходимо установить интервал от
     * километра "a" до километра "b", то
     * уровни усталости будут показывать
     * отношения в этом интервале.
     */

    var low: Double = 0.0

    var medium: Double = 0.0

    var high: Double = 0.toDouble()

    var critical: Double = 0.toDouble()

    /**
     * WarningAnalyser анализирует уровни усталости,
     * обрабатывает их, считает количество предупреждений
     * с устройства, также вы можете передавать данные
     * которые Вы можете
     */

    val warningAnalyser: WarningAnalyser = WarningAnalyser()

    var timeInterval: String? = null

    var distanceInterval: String? = null

    var maxPassedDistance: Double = 0.toDouble()

    init {
        @Temporary val dataGenerator = DataGenerator()
        val generatedInputData = dataGenerator.generateInputData()
        generatedInputData.forEach { inputData ->
            this.pushInputData(inputData.level
                    , inputData.passedDistance
                    , inputData.isAlarm
                    , WarningCallBack.EmptyWarningCallBack())
        }
    }


    /**
     * pushInputData(final double level, final double distance, final boolean isAlarm)
     * кладет данные, которые пришли с устройства в список,
     * предварительно их завернув в InputData, и передает их
     * WarningAnalyser.
     *
     * @param level    - уровень усталости в процентах;
     * @param distance - пройденный путь;
     * @param isAlarm  - сингнал с устройства, что человек засыпает.
     */

    fun pushInputData(level: Double, distance: Double, isAlarm: Boolean) {
        this.pushInputData(level, distance, isAlarm, WarningCallBack.EmptyWarningCallBack())
    }

    /**
     * pushInputData(final double level, final double distance, final boolean isAlarm)
     * делает тоже самое, что и предыдущий, но если Вы вдруг
     * захотите "callBacks", когда WarningAnalyser определяет
     * высокие уровени усталости, то лучше пользоваться эти методом.
     *
     * @param level           - уровень усталости в процентах;
     * @param distance        - пройденный путь;
     * @param isAlarm         - сингнал с устройства, что человек засыпает.
     * @param warningCallBack - callBack.
     */

    fun pushInputData(level: Double, distance: Double, isAlarm: Boolean, warningCallBack: WarningCallBack) {
        val inputData = InputData(level, distance, isAlarm)
        this.maxPassedDistance = inputData.passedDistance
        this.inputDataList.add(inputData)
        this.warningAnalyser.hasWarning(inputData, warningCallBack)
    }

    //Дальше идут конфигурации, это было сделано,
    //чтобы придать большую гибкость API, например,
    //если Вы хотите сделать просмотр какого-либо
    //участка пути по какому-либ критерию (Реализовано по километрам).

    /**
     * configureFullInterval() настраивает данные о поездке
     * в соответсвии с полным интервалом маршрута.
     */

    fun configureFullInterval() {
        this.configureIntervalByDistance("0", this.maxPassedDistance.toString() + "")
    }

    /**
     * configureIntervalByDistance(final String from, final String to)
     * настраивает данные о поезке в соответсвии с выбранными начальной
     * и конечной точки интервала.
     *
     * @param from - от какого километра нужно анализировать данные;
     * @param to   - до какого километра нужно анализировать данные.
     */

    fun configureIntervalByDistance(from: String, to: String) {
        val startPoint = java.lang.Double.parseDouble(from)
        val endPoint = java.lang.Double.parseDouble(to)
        if (endPoint - startPoint >= 0) {
            //Задаем фильт на посылки в соответствии с заданным интервалом:
            val filter = Function<InputData, Boolean> { inputData ->
                inputData.passedDistance in startPoint..endPoint
            }
            this.setDataSession(filter)
            this.distanceInterval = (endPoint - startPoint).toString() + " км"
        } else {
            throw IllegalArgumentException("Конечная точка не может быть меньше начальной")
        }
    }

    /**
     * configureIntervalByTime(final Date from. final Date to)
     * является полуабстрактным методом, на данный момент этот
     * метод не реализован, и если Вам потребуется его реализовать,
     * то просто унаследуйтесь от класса DataAnalyser и передаватите
     * этот метод.
     */

    @NonFinal
    fun configureIntervalByTime(from: Date, to: Date) {
        log.info("Stub")
    }

    /**
     * setDataSession() устанавливает данные об истории
     * в соответствии с полученными посылками, которые
     * будут отсеяны фильтром, переданным из
     * метода-настройки, который Вы выбрали на этапе
     * конфигурации.
     *
     * @param filter - фильтр по условию.
     */

    private fun setDataSession(filter: Function<InputData, Boolean>) {
        //Уровень усталости:
        var measurements = 0.0
        var sumLow = 0.0
        var sumMedium = 0.0
        var sumHigh = 0.0
        var sumCritical = 0.0
        //Начальное и конечное время:
        var wasFirst = false
        var wasLast = false
        var startMilliTime: Long = 0
        var endMilliTime: Long = 0
        for (i in this.inputDataList.indices) {
            val inputData = this.inputDataList[i]
            if (filter.apply(inputData)) {
                if (!wasFirst) {
                    startMilliTime = inputData.milliTime
                    wasFirst = true
                }
                if (inputData.isLowLevel()) sumLow++
                if (inputData.isMediumLevel()) sumMedium++
                if (inputData.isHighLevel()) sumHigh++
                if (inputData.isCriticalLevel()) sumCritical++
                measurements++
                if (i == this.inputDataList.size - 1) {
                    endMilliTime = inputData.milliTime
                }
            } else {
                if (!wasLast && wasFirst) {
                    endMilliTime = inputData.milliTime
                    wasLast = true
                }
            }
        }
        //Уровень усталости:
        this.low = sumLow / measurements
        this.medium = sumMedium / measurements
        this.high = sumHigh / measurements
        this.critical = sumCritical / measurements
        //Начальное и конечное время:
        val startTime = ConvenientTime(startMilliTime)
        val endTime = ConvenientTime(endMilliTime + Random().nextInt(500000000))
        val startDateString = startTime.convertToDateString()
        val endDateString = endTime.convertToDateString()
        //Считаем разницу по времени:
        val difference = endTime.date.time - startTime.date.time
        val allMinutes = difference.toInt() / (60 * 1000)
        val hours = allMinutes / 60
        val minutes = allMinutes - hours * 60
        val outputDate: String
        //Если даты начала поездки и окончания поездки совпадают:
        if (startDateString == endDateString) {
            outputDate = startDateString
        } else {
            outputDate = "$startDateString - $endDateString"
        }
        this.timeInterval = "$outputDate,\n$hours ч $minutes мин"
    }

    /**
     * Внутренний класс WarningAnalyser работает с предупреждениями:
     * он проверяет сигнал тревоги с устройства, а также уровень усталости,
     * записывает предупреждения, вызывает callBacks.
     */

    class WarningAnalyser internal constructor() {

        private val START_WARNING_NUMBER = 0

        private val MINUTE: Long = 60000

        var warningNumber: Int = 0

        var time: Long = 0

        init {
            this.warningNumber = START_WARNING_NUMBER
            this.time = System.currentTimeMillis()
        }

        internal fun hasWarning(inputData: InputData, callBack: WarningCallBack) {
            val currentTime = System.currentTimeMillis()
            //Будем тревожить водителя не реже чем в минуту:
            if (currentTime - this.time > MINUTE) {
                this.time = currentTime
                //Если пришел сигнал тревоги с устройства:
                if (inputData.isAlarm) {
                    this.warningNumber++
                    callBack.onAlarm(inputData)
                }
                //Если просто очень выской уровень усталости:
                if (inputData.isCriticalLevel()) {
                    callBack.onCriticalLevel(inputData)
                }
                //Если просто выской уровень усталости:
                if (inputData.isHighLevel()) {
                    callBack.onHighLevel(inputData)
                }
            }
        }
    }

    /**
     * getWarningNumber() является методом-делегатом
     * над полем warningNumber во внутреннем классе WarningAnalyser.
     *
     * @return число предупреждений.
     */

    fun getWarningNumber(): Int = this.warningAnalyser.warningNumber
}