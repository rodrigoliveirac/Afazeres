package com.rodcollab.afazeres.collections.domain

import com.rodcollab.afazeres.collections.TaskItem

interface GetCompletedTasksUseCase {

    suspend operator fun invoke(): List<TaskItem>
}