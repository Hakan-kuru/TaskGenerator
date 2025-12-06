package com.example.taskgenerator.data.repository_impl

import com.example.taskgenerator.data.dao.Main_task_dao
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.domain.repositories.Main_repository
import com.example.taskgenerator.utils.toDomain
import com.example.taskgenerator.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // Flow'un map operatörünü ilk kez kullanıyoruz.
import javax.inject.Inject

// Bu sınıf, domain'deki MainTaskRepository interface'ini Room (MainTaskDao) üzerinden implemente ediyor.
class Main_task_repository_impl @Inject constructor(
    private val mainTaskDao: Main_task_dao
) : Main_repository {

    override suspend fun deleteMainTaskWithSubTasks(mainTaskId: Long) {
        // Not: Eğer Room'da foreign key + CASCADE kullandıysan,
        mainTaskDao.deleteMainTask(mainTaskId)
    }

    override suspend fun toggleMainTaskDone(mainTaskId: Long) {
        mainTaskDao.toggleMainTaskDone(mainTaskId)
    }

    override fun getAllMainTasks(): Flow<List<Main_task>> {
        // Flow.map: Room'dan gelen Flow<List<MainTaskEntity>>'yi
        // Flow<List<MainTask>>'e dönüştürmek için kullanılıyor.
        return mainTaskDao.getAllMainTasks()
            .map { entityList ->
                entityList?.map { it.toDomain() } ?: emptyList()
            }
    }

    override fun getMainTaskById(id: Long): Flow<Main_task?> {
        return mainTaskDao.getMainTaskWithId(id).map { it?.toDomain() ?: null }
    }

    override fun getMainTaskWithSubTasks(id: Long): Flow<Main_with_sub_tasks?> {
        return mainTaskDao.getMainTaskWithSubTasks(id)
            .map { relationEntity ->
                relationEntity?.toDomain() // Burada MainTaskWithSubTasksEntity.toDomain() extension'ı çağrılıyor.
            }
    }

    override suspend fun insertMainTask(mainTask: Main_task): Long {
        return mainTaskDao.insertMainTask(mainTask.toEntity())
    }

    override suspend fun updateMainTask(mainTask: Main_task) {
        mainTaskDao.updateMainTask(mainTask.toEntity())
    }

    override fun getAllMainTasksWithSubTasks(): Flow<List<Main_with_sub_tasks>> {
        return mainTaskDao.getAllMainTasksWithSubTasks()
            .map { relationList ->
                // relationList: List<Main_task_with_sub_tasks_entity>
                relationList.map { it.toDomain() }
            }
    }
}
