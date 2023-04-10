package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.TaskItem
import kotlinx.coroutines.flow.Flow

interface GetCompletedTasksUseCase {
    operator fun invoke(): Flow<List<TaskItem>>
}