package com.example.taskgenerator.data

import kotlin.jvm.java
import android.content.Context
import androidx.room.Room

// Uygulama içinde AppDatabase örneğini oluşturmak için basit helper.
object App_database_provider {

    @Volatile
    private var INSTANCE: App_database? = null

    fun getDatabase(context: Context): App_database {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                App_database::class.java,
                "task_app.db" // veritabanı dosya adı
            ).build()

            INSTANCE = instance
            instance
        }
    }
}
