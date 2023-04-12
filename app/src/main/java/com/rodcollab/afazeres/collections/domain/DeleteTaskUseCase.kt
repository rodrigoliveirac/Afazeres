package com.rodcollab.afazeres.collections.domain

interface DeleteTaskUseCase {

    suspend operator fun invoke(taskId: String)
}
