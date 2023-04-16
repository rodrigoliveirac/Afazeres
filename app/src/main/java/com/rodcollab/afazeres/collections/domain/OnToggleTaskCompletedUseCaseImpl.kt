package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.core.repository.TasksRepository
import javax.inject.Inject

class OnToggleTaskCompletedUseCaseImpl @Inject constructor(private val tasksRepository: TasksRepository) :
    OnToggleTaskCompletedUseCase {
    override suspend fun invoke(taskId: String, isCompleted: Int) {
        tasksRepository.toggleTaskCompleted(taskId, isCompleted)
    }
}