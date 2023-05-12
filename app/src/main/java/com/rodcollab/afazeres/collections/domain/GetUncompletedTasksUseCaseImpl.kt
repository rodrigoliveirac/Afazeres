package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import com.rodcollab.afazeres.core.repository.TasksRepository
import javax.inject.Inject

class GetUncompletedTasksUseCaseImpl @Inject constructor(private val tasksRepository: TasksRepository) :
    GetUncompletedTasksUseCase {
    override suspend fun invoke(currentUserId: String, date: String): List<TaskItem> {
        return tasksRepository.uncompletedTasks(currentUserId, date).map { task ->
            TaskItem(
                id = task.taskId,
                title = task.taskTitle,
                category = task.taskCategory,
                date = task.taskDate,
                isCompleted = task.isCompleted,
                triggerTime = task.triggerTime,
                reminderTime = task.reminderTime,
                createdAt = task.createdAt
            )
        }
    }
}