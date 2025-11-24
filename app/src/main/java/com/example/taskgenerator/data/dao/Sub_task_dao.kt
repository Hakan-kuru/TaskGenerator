package com.example.taskgenerator.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskgenerator.data.entity.Sub_task_entity
import kotlinx.coroutines.flow.Flow

// sub_tasks tablosuna erişim sağlayan DAO.
@Dao
interface Sub_task_dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: Sub_task_entity): Long

    @Update
    suspend fun updateSubTask(subTask: Sub_task_entity)

    @Query("DELETE FROM sub_task WHERE id = :id")
    suspend fun deleteSubTaskById(id: Long)

    // Belirli bir mainTask'e bağlı alt görevleri almak için
    @Query(
        """
        SELECT * FROM sub_task
        WHERE mainTaskId = :mainTaskId
        ORDER BY orderInTask ASC, createdAt ASC
        """
    )
    fun getSubTasksForMainTask(mainTaskId: Long): Flow<List<Sub_task_entity>>
}
