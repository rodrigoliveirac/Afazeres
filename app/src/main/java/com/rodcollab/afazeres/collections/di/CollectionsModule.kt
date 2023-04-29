package com.rodcollab.afazeres.collections.di

import com.rodcollab.afazeres.collections.domain.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CollectionsModule {

    @Singleton
    @Binds
    abstract fun providesGetUncompletedTasksUseCase(impl: GetUncompletedTasksUseCaseImpl): GetUncompletedTasksUseCase

    @Singleton
    @Binds
    abstract fun providesGetCompletedTasksUseCase(impl: GetCompletedTasksUseCaseImpl): GetCompletedTasksUseCase

    @Singleton
    @Binds
    abstract fun providesToggleCompletedTaskUseCase(impl: OnToggleTaskCompletedUseCaseImpl): OnToggleTaskCompletedUseCase

    @Singleton
    @Binds
    abstract fun providesDeleteTaskUseCase(impl: DeleteTaskUseCaseImpl): DeleteTaskUseCase

    @Singleton
    @Binds
    abstract fun providesTasksWithAlarm(impl: GetTasksWithAlarmUseCaseImpl): GetTasksWithAlarmUseCase
}