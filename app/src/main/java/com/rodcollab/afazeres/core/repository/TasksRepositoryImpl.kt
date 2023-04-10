package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain
import java.util.UUID

class TasksRepositoryImpl : TasksRepository {

    private val tasks: MutableList<TaskDomain> = mutableListOf()

    override suspend fun uncompletedTasks() = tasks.filter { it.isCompleted }

    override suspend fun completedTasks() = tasks.filter { !it.isCompleted }

    override suspend fun toggleTaskCompleted(taskId: String) {
        tasks.map {
            if (it.taskId == taskId) {
                it.copy(isCompleted = !it.isCompleted)
            } else {
                it.copy()
            }
        }
    }

    override suspend fun add(
        taskId: String,
        taskTitle: String,
        taskCategory: String,
        taskDate: String
    ) {
        tasks.add(
            TaskDomain(
                taskId = UUID.randomUUID().toString(),
                taskTitle = taskTitle,
                taskCategory = taskCategory,
                taskDate = taskDate,
                isCompleted = false
            )
        )
    }
}