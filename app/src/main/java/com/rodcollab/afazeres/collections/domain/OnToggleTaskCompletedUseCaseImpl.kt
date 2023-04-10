package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.core.repository.TasksRepository

class OnToggleTaskCompletedUseCaseImpl(private val tasksRepository: TasksRepository) : OnToggleTaskCompletedUseCase {
    override suspend fun invoke(taskId: String) {
        tasksRepository.toggleTaskCompleted(taskId)
    }
}