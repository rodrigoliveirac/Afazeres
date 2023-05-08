package com.rodcollab.afazeres.util

import android.content.res.Resources
import com.rodcollab.afazeres.R

object TextUtil {

    fun getValueTimeInLong(textFromView: CharSequence): Long {
        val hourIndex = 0
        return textFromView.toString().split(":").mapIndexed { hour, text ->
            when (hour) {
                hourIndex -> text.toLong() * 3600000L
                else -> {
                    text.toLong() * 60000L
                }
            }
        }.sumOf { totalTime ->
            totalTime
        }

    }

    fun toLong(resources: Resources, reminderTimeText: String): Long {
        return when (reminderTimeText) {
            resources.getString(R.string.one_day_before, 1.toString()) -> 86400000L
            resources.getString(R.string.one_hour_before, 1.toString()) -> 3600000L
            resources.getString(R.string.thirty_min_before, 30.toString()) -> 1800000L
            else -> {
                0L
            }
        }
    }
    fun toString(resources: Resources, reminderTimeText: Long): String {
        return when (reminderTimeText) {
            86400000L -> resources.getString(R.string.one_day_from_now, 1.toString())
            3600000L -> resources.getString(R.string.one_hour_from_now, 1.toString())
            1800000L -> resources.getString(R.string.thirty_min_from_now, 30.toString())
            else -> {
                ""
            }
        }
    }
}