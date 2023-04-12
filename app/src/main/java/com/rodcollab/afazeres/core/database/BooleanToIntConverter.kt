package com.rodcollab.afazeres.core.database

import androidx.room.TypeConverter

class BooleanToIntConverter {
    @TypeConverter
    fun fromBoolean(value: Boolean): Int {
        return if (value) 1 else 0
    }

    @TypeConverter
    fun toBoolean(value: Int): Boolean {
        return value != 0
    }
}