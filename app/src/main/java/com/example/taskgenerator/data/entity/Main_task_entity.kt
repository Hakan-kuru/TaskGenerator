package com.example.taskgenerator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "main_task")
data class Main_task_entity(
    @PrimaryKey(autoGenerate = true)
    val mainTaskId: Long = 0L,
    var title: String,
    var description: String,
    val taskType: String? =null,
    var isDone: Boolean = false,
    var targetCount: Int? =null,
    var currentCount: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)
