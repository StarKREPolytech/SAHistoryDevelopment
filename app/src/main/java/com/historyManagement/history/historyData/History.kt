package com.historyManagement.history.historyData

/**
 * @author Игорь Гулькин 24.04.2018
 *         <p>
 *         Класс HistoryItemView хранит в себе
 *         подробную информацию о поездке.
 */

class History {

    /**
     * 1.) headline - заголовок истории;
     * 2.) description - описание истории;
     * 3.) dataAnalyser - аналирирует и
     * хранит данные, которые пришли с устройства.
     */

    var headline: String? = null

    var description: String? = null

    val dataAnalyser: DataAnalyser = DataAnalyser()

    /**
     * pullDescription() - немного необычный геттер,
     * потому что при первом вызове происходит
     * инициализация описания истории.
     *
     *
     * (Конечно потом будет вызываться проверка на null,
     * но на performance это не повлияет)
     *
     * @return описание истории.
     */

    fun pullDescription(): String {
        if (this.description == null) {
            this.description = this.buildDescription()
        }
        return this.description!!
    }

    /**
     * buildDescription() создает описание к истории
     *
     * @return описание истории.
     */

    private fun buildDescription(): String {
        this.dataAnalyser.configureFullInterval()
        val fullTime = this.dataAnalyser.timeInterval
        val fullDistance = this.dataAnalyser.distanceInterval
        return "$fullTime, $fullDistance"
    }

    override fun toString(): String = this.headline!!
}