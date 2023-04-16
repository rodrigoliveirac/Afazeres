package com.rodcollab.afazeres.di

import com.rodcollab.afazeres.core.repository.TasksRepository
import com.rodcollab.afazeres.core.repository.TasksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun providesTasksRepository(impl: TasksRepositoryImpl) : TasksRepository
}