package com.example.taskgenerator.data.di

import android.content.Context
import androidx.room.Room
import com.example.taskgenerator.data.App_database
import com.example.taskgenerator.data.dao.Main_task_dao
import com.example.taskgenerator.data.dao.Sub_task_dao
import com.example.taskgenerator.domain.repositories.Time_provider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): App_database =
        Room.databaseBuilder(
            context,
            App_database::class.java,
            "task_app.db"
        ).build()

    @Provides
    fun provideMainTaskDao(db: App_database): Main_task_dao = db.mainTaskDao()

    @Provides
    fun provideSubTaskDao(db: App_database): Sub_task_dao = db.subTaskDao()


    @Provides
    fun provideTimeProvider(): Time_provider = Android_time_provider()

}