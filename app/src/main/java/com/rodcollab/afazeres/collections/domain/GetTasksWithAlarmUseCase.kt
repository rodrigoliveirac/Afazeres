package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface GetTasksWithAlarmUseCase {
    operator fun invoke(currentUserId:String): Flow<List<TaskItem>>
}