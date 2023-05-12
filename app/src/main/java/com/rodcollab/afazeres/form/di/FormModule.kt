package com.rodcollab.afazeres.form.di

import com.rodcollab.afazeres.form.domain.GetCurrentUserIdUseCase
import com.rodcollab.afazeres.form.domain.GetCurrentUserIdUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FormModule {

    @Singleton
    @Binds
    abstract fun providesGetCurrentUserIdUseCase(impl: GetCurrentUserIdUseCaseImpl): GetCurrentUserIdUseCase
}