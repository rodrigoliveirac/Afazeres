package com.rodcollab.afazeres.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodcollab.afazeres.core.database.BooleanToIntConverter

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "task_title") val title: String,
    @ColumnInfo(name = "task_category") val category: String,
    @ColumnInfo(name = "task_date") val date: String,
    @TypeConverters(BooleanToIntConverter::class)
    @ColumnInfo(name = "task_isCompleted") val isCompleted: Boolean
)