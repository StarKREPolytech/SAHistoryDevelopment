package com.historyManagement.history.historyData.convinientTime

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Класс ConvenientTime позволяет работать как с днями, так и с часами.
 */

class ConvenientTime(milliTime: Long) {

    val date: Date = Date(milliTime)

    @SuppressLint("SimpleDateFormat")
    private val timeFormat: DateFormat = SimpleDateFormat("HH:mm")

    /**
     * convertToLongString() собирает полную информацию о дне.
     *
     * @return дату и время.
     */

    private fun convertToLongString(): String = this.convertToDateString() + " " + this.convertToTimeString()

    /**
     * convertToDateString() собирает информацию о дне.
     *
     * @return дату.
     */

    @SuppressLint("SimpleDateFormat")
    fun convertToDateString(): String = SimpleDateFormat("dd.MM.yyyy").format(this.date)

    /**
     * convertToTimeString() собирает информацию о часах.
     *
     * @return время.
     */

    @SuppressLint("SimpleDateFormat")
    private fun convertToTimeString(): String = SimpleDateFormat("HH:mm").format(this.date)
}