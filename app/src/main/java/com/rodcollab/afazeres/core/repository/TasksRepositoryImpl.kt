package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.database.dao.TaskDao
import com.rodcollab.afazeres.core.database.entity.Task
import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(private val dao: TaskDao) : TasksRepository {

    override  fun uncompletedTasks(): Flow<List<TaskDomain>> {
        return dao.fetchUncompletedTasks().map { tasks ->
            tasks.map {
                TaskDomain(
                    taskId = it.uuid,
                    taskTitle = it.title,
                    taskCategory = it.category,
                    taskDate = it.date,
                    isCompleted = it.isCompleted,
                    alarmActive = it.alarmActive,
                    reminderTime = it.reminderTime
                )
            }
        }
    }

    override fun completedTasks(): Flow<List<TaskDomain>> {
        return dao.fetchCompletedTasks().map { tasks ->
            tasks.map {
                TaskDomain(
                    taskId = it.uuid,
                    taskTitle = it.title,
                    taskCategory = it.category,
                    taskDate = it.date,
                    isCompleted = it.isCompleted,
                    alarmActive = it.alarmActive,
                    reminderTime = it.reminderTime
                )
            }
        }
    }

    override suspend fun toggleTaskCompleted(taskId: String, isCompleted: Int) {

        dao.onToggleChecked(taskId, isCompleted)
    }

    override suspend fun add(
        taskTitle: String,
        taskCategory: String,
        taskDate: String,
        taskTime: Long?,
        alarmActive: Boolean,
        reminderTime:Long?
    ) {
        dao.insert(

            Task(
                uuid = UUID.randomUUID().toString(),
                title = taskTitle,
                category = taskCategory,
                date = taskDate,
                time = taskTime,
                isCompleted = false,
                alarmActive = alarmActive,
                reminderTime = reminderTime
            )
        )
    }

    override suspend fun delete(taskId: String) {
        dao.delete(taskId)
    }
}