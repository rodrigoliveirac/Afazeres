package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface GetCompletedTasksUseCase {
    suspend operator fun invoke(date: String): Flow<List<TaskItem>>
}