package com.rodcollab.afazeres.form.domain

import com.rodcollab.afazeres.auth.service.AccountService
import javax.inject.Inject

class GetCurrentUserIdUseCaseImpl @Inject constructor(private val accountService: AccountService) :
    GetCurrentUserIdUseCase {
    override suspend fun invoke(): String {
        return accountService.currentUserId
    }
}