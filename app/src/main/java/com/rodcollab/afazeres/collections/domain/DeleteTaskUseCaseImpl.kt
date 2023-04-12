package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.core.repository.TasksRepository

class DeleteTaskUseCaseImpl(private val repository: TasksRepository) : DeleteTaskUseCase {
    override suspend fun invoke(taskId: String) {
        repository.delete(taskId)
    }

}
