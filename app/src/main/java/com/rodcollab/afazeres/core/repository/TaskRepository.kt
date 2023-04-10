package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain

interface TasksRepository {

    suspend fun uncompletedTasks(): List<TaskDomain>

    suspend fun completedTasks(): List<TaskDomain>

    suspend fun toggleTaskCompleted(taskId: String)

    suspend fun add(taskId: String, taskTitle: String, taskCategory: String, taskDate: String)
}