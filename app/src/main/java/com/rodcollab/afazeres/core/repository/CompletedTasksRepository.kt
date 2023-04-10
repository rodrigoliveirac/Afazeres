package com.rodcollab.afazeres.core.repository

interface CompletedTasksRepository {

    suspend fun add(taskId: String, title)
}
