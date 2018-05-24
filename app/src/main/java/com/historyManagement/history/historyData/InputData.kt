package com.historyManagement.history.historyData

import lombok.ToString

@ToString(includeFieldNames = false, of = arrayOf("level"))
class InputData (val level: Double, val passedDistance: Double, val isAlarm: Boolean) {

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
}