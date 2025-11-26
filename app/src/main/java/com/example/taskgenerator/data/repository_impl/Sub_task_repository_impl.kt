package com.example.taskgenerator.data.repository_impl


import com.example.taskgenerator.data.dao.Sub_task_dao
import com.example.taskgenerator.data.entity.Sub_task_entity
import com.example.taskgenerator.domain.model.Sub_task
import com.example.taskgenerator.domain.repositories.Sub_repository
import com.example.taskgenerator.utils.toDomain
import com.example.taskgenerator.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Sub_repository'nin data katmanı implementasyonu
class Sub_task_repository_impl @Inject constructor(
    private val subTaskDao: Sub_task_dao         // <-- DAO Hilt'ten gelecek
) : Sub_repository {

    // Belirli bir mainTask'e bağlı tüm subTask'ler
    override fun getSubTasksByMainTaskId(mainTaskId: Long): Flow<List<Sub_task>> {
        return subTaskDao.getSubTasksForMainTask(mainTaskId) // Flow<List<Sub_task_entity>>
            .map { entities -> entities.map { it.toDomain() } }
    }

    // Tek bir subTask'i id ile gözlemlemek için
    override fun getSubTaskById(id: Long): Flow<Sub_task?> {
        return subTaskDao.getSubTaskWithId(id) // Flow<Sub_task_entity?>
            .map { entity -> entity?.toDomain() as Sub_task? }
    }

    // Yeni bir subTask eklemek
    override suspend fun insertSubTask(subTask: Sub_task): Long {
        return subTaskDao.insertSubTask(subTask.toEntity())
    }

    // Var olan subTask'i güncellemek
    override suspend fun updateSubTask(subTask: Sub_task) {
        subTaskDao.updateSubTask(subTask.toEntity())
    }

    // SubTask'i tamamen silmek
    override suspend fun deleteSubTask(subTaskId: Long) {
        subTaskDao.deleteSubTaskById(subTaskId)
    }

    override suspend fun updateSubTaskProgress(
        subTaskId: Long,
        isDone: Boolean?,
        currentCount: Int?
    ) {
        subTaskDao.updateSubTaskProgress(
            subTaskId = subTaskId,
            isDone = isDone,
            currentCount = currentCount
        )
    }
}