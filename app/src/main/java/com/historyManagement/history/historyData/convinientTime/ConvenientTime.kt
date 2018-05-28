package com.historyManagement.history.historyData.convinientTime

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ConvenientTime(milliTime: Long) {

    val date: Date = Date(milliTime)

    @SuppressLint("SimpleDateFormat")
    private val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy")

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

    fun convertToDateString(): String = this.dateFormat.format(this.date)

    /**
     * convertToTimeString() собирает информацию о часах.
     *
     * @return время.
     */

    private fun convertToTimeString(): String = this.timeFormat.format(this.date)
}