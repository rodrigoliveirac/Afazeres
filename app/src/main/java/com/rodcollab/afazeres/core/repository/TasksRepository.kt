package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    suspend fun uncompletedTasks(date: String): List<TaskDomain>

    suspend fun completedTasks(date: String): List<TaskDomain>

    fun tasksWithAlarm(): Flow<List<TaskDomain>>

    suspend fun toggleTaskCompleted(taskId: String, isCompleted: Int)

    suspend fun add(
        taskTitle: String,
        taskCategory: String,
        taskDate: Long?,
        taskTime: String?,
        alarmActive: Boolean,
        reminderTime: Long?,
        triggerTime: Long?
    )

    suspend fun delete(taskId: String)
}