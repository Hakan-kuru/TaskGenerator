package com.example.taskgenerator.domain.repositories

import com.example.taskgenerator.data.entity.Main_task_with_sub_tasks_entity
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import kotlinx.coroutines.flow.Flow

// Repository interface: domain katmanının data katmanından (Room, API, vs.) bağımsız olmasını sağlar.
interface Main_repository {

    // Tüm main task'leri sürekli olarak gözlemlemek için kullanılır.
    fun getAllMainTasks(): Flow<List<Main_task>>

    // Tek bir main task'i id ile gözlemlemek için.
    fun getMainTaskById(id: Long): Flow<Main_task?>

    // Main task + ona bağlı tüm subTask'leri tek seferde almak için.
    fun getMainTaskWithSubTasks(id: Long): Flow<Main_with_sub_tasks?>

    // Yeni bir main task eklemek için.
    suspend fun insertMainTask(mainTask: Main_task): Long

    // Var olan main task'i güncellemek için.
    suspend fun updateMainTask(mainTask: Main_task)
}
