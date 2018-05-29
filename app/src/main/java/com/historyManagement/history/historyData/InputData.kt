package com.historyManagement.history.historyData

import com.annotations.Temporary

/**
 * @author Игорь Гулькин создал класс хрен знет когда.
 *
 * Класс InputData хранит в себе посылку во время сессии.
 *
 * P. S. Я не знаю где брать данные, поэтому я сам себе генерю посылки, кладу их в аналайзер,
 * чтобы работать с ними в выбранном окне историй.
 */

@Temporary
data class InputData (val level: Double, val passedDistance: Double, val isAlarm: Boolean) {

    /**
     * Время пришедшей посылки.
     */

    val milliTime: Long = System.currentTimeMillis()

    init {
        if (this.level < 0 || this.level > 100) {
            throw IllegalTirednessLevelException()
        }
    }

    internal fun isLowLevel(): Boolean = this.level >= 0 && this.level < 40

    internal fun isMediumLevel(): Boolean = this.level >= 40 && this.level < 60

    internal fun isHighLevel(): Boolean = this.level >= 60 && this.level < 80

    internal fun isCriticalLevel(): Boolean = this.level >= 80

    private class IllegalTirednessLevelException
        : RuntimeException("Полученный уровень усталости должен быть в пределах от 0 до 100%")

    override fun toString(): String = "${this.level}"
}