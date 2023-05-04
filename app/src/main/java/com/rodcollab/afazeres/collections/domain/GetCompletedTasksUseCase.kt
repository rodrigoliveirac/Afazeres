package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface GetCompletedTasksUseCase {
    operator fun invoke(date: String): Flow<List<TaskItem>>
}