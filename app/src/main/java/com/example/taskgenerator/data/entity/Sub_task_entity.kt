package com.example.taskgenerator.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_task",
    foreignKeys = [
        ForeignKey(
            entity = Main_task_entity::class,
            parentColumns = ["mainTaskId"],
            childColumns = ["mainTaskId"],
            onDelete = ForeignKey.CASCADE // MainTask silinince alt görevler de silinsin
        )
    ],
    indices = [
        Index("mainTaskId") // mainTaskId üzerinde index performans için
    ]
)
data class Sub_task_entity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var mainTaskId: Long,
    var title: String,
    var description: String,
    val taskType: String? = null,
    var isDone: Boolean = false,
    var targetCount: Int? =null,
    var currentCount: Int? = null,
    var orderInTask: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

