package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.core.repository.TasksRepository
import javax.inject.Inject

class DeleteTaskUseCaseImpl @Inject constructor(private val repository: TasksRepository) :
    DeleteTaskUseCase {
    override suspend fun invoke(taskId: String) {
        repository.delete(taskId)
    }

}
