package com.example.taskgenerator.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.taskgenerator.data.entity.Main_task_with_sub_tasks_entity
import com.example.taskgenerator.data.entity.Main_task_entity
import kotlinx.coroutines.flow.Flow

@Dao
interface Main_task_dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMainTask(task: Main_task_entity): Long

    @Update
    suspend fun updateMainTask(task: Main_task_entity)

    @Query("DELETE FROM main_task WHERE mainTaskId = :id")
    suspend fun DeleteOneMainTask(id: Long)

    // Tüm ana görevleri basit şekilde almak için
    @Query("SELECT * FROM main_task ORDER BY createdAt DESC")
    fun getAllMainTasks(): Flow<List<Main_task_entity>>

    @Query("SELECT * FROM main_task WHERE mainTaskId = :id")
    fun getMainTaskWithId(id: Long): Flow<Main_task_entity?>

    // Ana görev ve alt görevlerini birlikte almak için
    @Transaction
    @Query("SELECT * FROM main_task ORDER BY createdAt DESC")
    fun getAllMainTasksWithSubTasks(): Flow<List<Main_task_with_sub_tasks_entity>>

    @Transaction
    @Query("SELECT * FROM main_task WHERE mainTaskId = :id")
    fun getMainTaskWithSubTasks(id: Long): Flow<Main_task_with_sub_tasks_entity?>
}