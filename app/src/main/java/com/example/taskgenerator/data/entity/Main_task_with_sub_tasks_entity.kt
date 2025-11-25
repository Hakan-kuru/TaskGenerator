package com.example.taskgenerator.data.entity


import androidx.room.Embedded
import androidx.room.Relation

// Room'un @Relation özelliğini kullanarak
// bir MainTask ile ona bağlı tüm SubTask'leri tek seferde almak için kullandığımız model.
data class Main_task_with_sub_tasks_entity(
    @Embedded
    val mainTask: Main_task_entity,

    @Relation(
        parentColumn = "mainTaskId",
        entityColumn = "mainTaskId"
    )
    val subTasks: List<Sub_task_entity>
)