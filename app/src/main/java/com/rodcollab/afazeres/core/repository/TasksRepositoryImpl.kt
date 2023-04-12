package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.database.AppDatabase
import com.rodcollab.afazeres.core.database.entity.Task
import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class TasksRepositoryImpl(appDatabase: AppDatabase) : TasksRepository {

    private val tasks = appDatabase.taskDao()

    override  fun uncompletedTasks(): Flow<List<TaskDomain>> {
        return tasks.fetchUncompletedTasks().map { tasks ->
            tasks.map {
                TaskDomain(
                    taskId = it.uuid,
                    taskTitle = it.title,
                    taskCategory = it.category,
                    taskDate = it.date,
                    isCompleted = it.isCompleted
                )
            }
        }
    }

    override fun completedTasks(): Flow<List<TaskDomain>> {
        return tasks.fetchCompletedTasks().map { tasks ->
            tasks.map {
                TaskDomain(
                    taskId = it.uuid,
                    taskTitle = it.title,
                    taskCategory = it.category,
                    taskDate = it.date,
                    isCompleted = it.isCompleted
                )
            }
        }
    }

    override suspend fun toggleTaskCompleted(taskId: String, isCompleted: Int) {

        tasks.onToggleChecked(taskId, isCompleted)
    }

    override suspend fun add(
        taskTitle: String,
        taskCategory: String,
        taskDate: String
    ) {
        tasks.insert(

            Task(
                uuid = UUID.randomUUID().toString(),
                title = taskTitle,
                category = taskCategory,
                date = taskDate,
                isCompleted = false
            )
        )
    }

    override suspend fun delete(taskId: String) {
        tasks.delete(taskId)
    }
}