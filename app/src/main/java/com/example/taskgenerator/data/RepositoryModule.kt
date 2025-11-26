package com.example.taskgenerator.data


import com.example.taskgenerator.data.repository_impl.Main_task_repository_impl
import com.example.taskgenerator.data.repository_impl.Sub_task_repository_impl
import com.example.taskgenerator.domain.repositories.Main_repository
import com.example.taskgenerator.domain.repositories.Sub_repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMainRepository(
        impl: Main_task_repository_impl
    ): Main_repository

    @Binds
    @Singleton
    abstract fun bindSubRepository(
        impl: Sub_task_repository_impl
    ): Sub_repository
}