package com.rodcollab.afazeres.di

import com.rodcollab.afazeres.auth.service.AccountService
import com.rodcollab.afazeres.auth.service.AccountServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Singleton
    @Binds
    abstract fun accountService(accountService: AccountServiceImpl) : AccountService
}