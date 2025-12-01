package com.example.taskgenerator
import android.net.Uri

object Routes {

    // Ekran isimleri (route stringleri)
    const val MAIN = "main"
    const val CREATE_MAIN_TASK = "add_main_task"
    const val ADD_SUB_TASK = "add_sub_task"

    // Argüman anahtarları
    const val ARG_PARENT_TASK_ID = "parentTaskId"
    const val ARG_PARENT_TASK_TITLE = "parentTaskTitle"

    // AddSubTask ekranı için route pattern (NavHost içinde kullanılacak)
    const val ADD_SUB_TASK_ROUTE =
        "$ADD_SUB_TASK/{$ARG_PARENT_TASK_ID}?$ARG_PARENT_TASK_TITLE={$ARG_PARENT_TASK_TITLE}"

    // AddSubTask ekranına giderken kullanılacak helper fonksiyon
    fun addSubTaskRoute(parentTaskId: Long, parentTaskTitle: String? = null): String {
        return if (parentTaskTitle.isNullOrBlank()) {
            "$ADD_SUB_TASK/$parentTaskId"
        } else {
            // Query param için encode önemli (boşluk, Türkçe karakter vs.)
            "$ADD_SUB_TASK/$parentTaskId?$ARG_PARENT_TASK_TITLE=${Uri.encode(parentTaskTitle)}"
        }
    }
}