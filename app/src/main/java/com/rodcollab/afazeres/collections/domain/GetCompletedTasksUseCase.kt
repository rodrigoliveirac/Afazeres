package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.TaskItem
import com.rodcollab.afazeres.core.model.TaskDomain

interface GetCompletedTasksUseCase {

    suspend operator fun invoke(): List<TaskItem>
}