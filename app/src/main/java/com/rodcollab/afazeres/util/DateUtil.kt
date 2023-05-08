package com.rodcollab.afazeres.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtil {

    fun dateTimeFormatter() =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now())
            .toString()

    @SuppressLint("SimpleDateFormat")
    fun simpleDateFormat(): String = SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().time)

    fun getDateInMillisFrom(value: Long): String {
        val date = getDate(value)
        val day = (date.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
        val month = (date.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = date.get(Calendar.YEAR).toString()
        return "$day/$month/$year"
    }

    fun getStringFrom(value: Long): String {
        val calendar = getDate(value)
        val date = calendar.time
        val formatter =
            DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault())
        return DateTimeFormatter.ofPattern("MMM dd, yyyy").format(
            LocalDate.parse(formatter.format(date.toInstant()).toString(), formatter)
        ).toString()
    }

    private fun getDate(value: Long): Calendar {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.timeInMillis = value
        return calendarInstance
    }

}