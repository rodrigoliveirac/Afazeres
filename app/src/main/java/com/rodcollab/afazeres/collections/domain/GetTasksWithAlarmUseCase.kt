package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface GetTasksWithAlarmUseCase {
    suspend operator fun invoke(): Flow<List<TaskItem>>
}