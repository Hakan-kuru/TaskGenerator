package com.example.taskgenerator.data.repository_impl

import com.example.taskgenerator.data.dao.Main_task_dao
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.domain.repositories.Main_repository
import com.example.taskgenerator.utils.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // Flow'un map operatörünü ilk kez kullanıyoruz.

// Bu sınıf, domain'deki MainTaskRepository interface'ini Room (MainTaskDao) üzerinden implemente ediyor.
class Main_task_repository_impl(
    private val mainTaskDao: Main_task_dao
) : Main_repository {

    override fun getAllMainTasks(): Flow<List<Main_task>> {
        // Flow.map: Room'dan gelen Flow<List<MainTaskEntity>>'yi
        // Flow<List<MainTask>>'e dönüştürmek için kullanılıyor.
        return mainTaskDao.getAllMainTasks()
            .map { entityList ->
                entityList.map { it.toDomain() }
            }
    }

    override fun getMainTaskById(id: Long): Flow<Main_task?> {
        return mainTaskDao.getAllMainTasks()
            .map { entity ->
                entity.toDomain()
            }
    }

    override fun getMainTaskWithSubTasks(id: Long): Flow<Main_with_sub_tasks?> {
        return mainTaskDao.getMainTaskWithSubTasks(id)
            .map { relationEntity ->
                relationEntity?.toDomain() // Burada MainTaskWithSubTasksEntity.toDomain() extension'ı çağrılıyor.
            }
    }

    override suspend fun insertMainTask(mainTask: Main_task) {
        mainTaskDao.insertMainTask(mainTask)
    }

    override suspend fun updateMainTask(mainTask: Main_task) {
        mainTaskDao.updateMainTask(mainTask)
    }

    override suspend fun deleteMainTask(mainTaskId: Long) {
        mainTaskDao.deleteMainTask(mainTaskId)
    }
}
