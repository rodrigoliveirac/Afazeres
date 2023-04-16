package com.rodcollab.afazeres.di

import android.app.Application
import com.rodcollab.afazeres.core.database.AppDatabase
import com.rodcollab.afazeres.core.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun providesRoomDatabase(application: Application) : AppDatabase {
        return AppDatabase.getInstance(application)
    }

    @Singleton
    @Provides
    fun providesTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
}