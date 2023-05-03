package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface GetUncompletedTasksUseCase {
    suspend operator fun invoke(date: String): Flow<List<TaskItem>>
}