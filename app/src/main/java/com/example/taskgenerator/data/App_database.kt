package com.example.taskgenerator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskgenerator.data.dao.Main_task_dao
import com.example.taskgenerator.data.dao.Sub_task_dao
import com.example.taskgenerator.data.entity.Main_task_entity
import com.example.taskgenerator.data.entity.Sub_task_entity

// Room veritabanı tanımı.
// Bu projede ilk defa RoomDatabase kalıtımı kullanıyoruz: Room buradan gerçek DB implementasyonunu üretir.
@Database(
    entities = [
        Main_task_entity::class,
        Sub_task_entity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class App_database : RoomDatabase() {

    abstract fun mainTaskDao(): Main_task_dao
    abstract fun subTaskDao(): Sub_task_dao
}
