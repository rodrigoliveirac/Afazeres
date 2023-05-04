package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun uncompletedTasks(date: String): Flow<List<TaskDomain>>

    fun completedTasks(date: String): Flow<List<TaskDomain>>

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