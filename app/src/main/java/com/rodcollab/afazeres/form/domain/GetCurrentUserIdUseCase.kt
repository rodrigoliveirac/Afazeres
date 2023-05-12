package com.rodcollab.afazeres.form.domain

interface GetCurrentUserIdUseCase {

    suspend operator fun invoke(): String
}
