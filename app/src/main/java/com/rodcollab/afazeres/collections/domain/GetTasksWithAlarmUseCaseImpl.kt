package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import com.rodcollab.afazeres.core.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTasksWithAlarmUseCaseImpl @Inject constructor(private val tasks: TasksRepository) :
    GetTasksWithAlarmUseCase {
    override suspend fun invoke(date: String): Flow<List<TaskItem>> {
        return tasks.uncompletedTasks(date).map { tasks ->
            tasks
                .filter { it.alarmActive }
                .map {
                    TaskItem(
                        id = it.taskId,
                        title = it.taskTitle,
                        category = it.taskCategory,
                        isCompleted = it.isCompleted,
                        date = it.taskDate,
                        triggerTime = it.triggerTime,
                        reminderTime = it.reminderTime
                    )
                }
        }
    }
}

