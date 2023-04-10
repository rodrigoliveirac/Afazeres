package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.TaskDomain
import java.util.UUID

class TasksRepositoryImpl : TasksRepository {

    private var tasks: MutableList<TaskDomain> = mutableListOf()

    private var taskFlow: MutableStateFlow<List<TaskDomain>> = MutableStateFlow(tasks)

    override fun uncompletedTasks(): Flow<List<TaskDomain>> {
        return taskFlow.map { tasks ->
            tasks.filter { !it.isCompleted }
        }
    }

    override fun completedTasks() : Flow<List<TaskDomain>> {
        return taskFlow.map { tasks ->
            tasks.filter { it.isCompleted }
        }
    }

    override suspend fun toggleTaskCompleted(taskId: String) {

        withContext(Dispatchers.IO) {
            tasks = tasks.map {
                if (it.taskId == taskId) {
                    it.copy(isCompleted = !it.isCompleted)
                } else {
                    it.copy()
                }
            } as MutableList<TaskDomain>
            taskFlow.value = tasks
        }
    }

    override suspend fun add(
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

        taskFlow.value = tasks.toList()
    }
}