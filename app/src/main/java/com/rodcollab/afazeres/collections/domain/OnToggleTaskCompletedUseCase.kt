package com.rodcollab.afazeres.collections.domain

interface OnToggleTaskCompletedUseCase {

    suspend operator fun invoke(taskId: String)
}
