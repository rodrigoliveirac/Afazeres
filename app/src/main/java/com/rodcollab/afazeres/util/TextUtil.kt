package com.rodcollab.afazeres.util

import android.content.res.Resources
import com.rodcollab.afazeres.R

object TextUtil {

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