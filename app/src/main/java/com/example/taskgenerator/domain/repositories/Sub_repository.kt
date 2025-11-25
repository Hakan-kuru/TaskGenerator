package com.example.taskgenerator.domain.repositories

import kotlinx.coroutines.flow.Flow

// SubTask'ler için domain katmanındaki repository interface.
interface Sub_repository {

    // Belirli bir mainTask'e bağlı tüm subTask'leri gözlemlemek için.
    fun getSubTasksByMainTaskId(mainTaskId: Long): Flow<List<Sub_task>>

    // Tek bir subTask'i id ile gözlemlemek için.
    fun getSubTaskById(id: Long): Flow<Sub_task?>

    // Yeni bir subTask eklemek için.
    suspend fun insertSubTask(subTask: Sub_task): Long

    // Var olan subTask'i güncellemek için.
    suspend fun updateSubTask(subTask: Sub_task)

    // SubTask'i tamamen silmek için.
    suspend fun deleteSubTask(subTaskId: Long)

    // Tamamlandı bilgisi veya progress güncellemesi gibi,
    // sadece state değiştirmek için kullanabileceğin yardımcı fonksiyon.
    suspend fun updateSubTaskProgress(
        subTaskId: Long,
        isDone: Boolean? = null,
        currentCount: Int? = null
    )
}
