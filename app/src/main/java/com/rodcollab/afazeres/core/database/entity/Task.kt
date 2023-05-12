package com.rodcollab.afazeres.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodcollab.afazeres.core.database.BooleanToIntConverter

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "task_title") val title: String,
    @ColumnInfo(name = "task_category") val category: String,
    @ColumnInfo(name = "task_date") val date: String,
    @ColumnInfo(name = "task_time") val time: String?,
    @TypeConverters(BooleanToIntConverter::class)
    @ColumnInfo(name = "task_isCompleted") val isCompleted: Boolean,
    @ColumnInfo(name = "task_alarmActive") val alarmActive: Boolean,
    @ColumnInfo(name = "task_reminderTime") val reminderTime: Long?,
    @ColumnInfo(name = "task_triggerTime") val triggerTime: Long?
)
