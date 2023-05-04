package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import com.rodcollab.afazeres.core.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUncompletedTasksUseCaseImpl @Inject constructor(private val tasksRepository: TasksRepository) :
    GetUncompletedTasksUseCase {
    override fun invoke(date: String): Flow<List<TaskItem>> {
        return tasksRepository.uncompletedTasks(date).map { tasks ->
            tasks.map { task ->
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
}