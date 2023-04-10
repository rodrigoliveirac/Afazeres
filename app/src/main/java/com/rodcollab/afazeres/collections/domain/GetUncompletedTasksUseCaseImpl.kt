package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.TaskItem
import com.rodcollab.afazeres.core.repository.TasksRepository

class GetUncompletedTasksUseCaseImpl(private val tasksRepository: TasksRepository) : GetUncompletedTasksUseCase {
    override suspend fun invoke(): List<TaskItem> {
        return tasksRepository.uncompletedTasks().map {

            TaskItem(
                id = it.taskId,
                title = it.taskTitle,
                category = it.taskCategory,
                date = it.taskDate,
                isCompleted = it.isCompleted,
            )
        }
    }
}