package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.model.TaskItem

interface GetCompletedTasksUseCase {
    suspend operator fun invoke(date: String): List<TaskItem>
}