package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    suspend fun uncompletedTasks(currentUserId: String,date: String): List<TaskDomain>

    suspend fun completedTasks(currentUserId: String, date: String): List<TaskDomain>

    fun tasksWithAlarm(currentUserId: String): Flow<List<TaskDomain>>

    suspend fun toggleTaskCompleted(taskId: String, isCompleted: Int)

    suspend fun add(
        currentUserId: String,
        taskTitle: String,
        taskCategory: String,
        taskDate: Long?,
        taskTime: String?,
        alarmActive: Boolean,
        reminderTime: Long?,
        triggerTime: Long?,
    )

    suspend fun delete(taskId: String)
}