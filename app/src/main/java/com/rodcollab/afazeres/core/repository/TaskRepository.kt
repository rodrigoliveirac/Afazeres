package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun uncompletedTasks(): Flow<List<TaskDomain>>

    fun completedTasks(): Flow<List<TaskDomain>>

    suspend fun toggleTaskCompleted(taskId: String, isCompleted: Int)

    suspend fun add(taskTitle: String, taskCategory: String, taskDate: String, taskTime: Long?, alarmActive: Boolean, reminderTime: Long?)
    suspend fun delete(taskId: String)
}