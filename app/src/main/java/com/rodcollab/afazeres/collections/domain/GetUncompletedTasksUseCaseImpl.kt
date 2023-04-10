package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.TaskItem
import com.rodcollab.afazeres.core.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUncompletedTasksUseCaseImpl(private val tasksRepository: TasksRepository) : GetUncompletedTasksUseCase {
    override fun invoke(): Flow<List<TaskItem>> {
        return tasksRepository.uncompletedTasks().map { tasks ->
            tasks.map { task ->
                TaskItem(
                    id = task.taskId,
                    title = task.taskTitle,
                    category = task.taskCategory,
                    date = task.taskDate,
                    isCompleted = task.isCompleted
                )
            }
        }
    }
}