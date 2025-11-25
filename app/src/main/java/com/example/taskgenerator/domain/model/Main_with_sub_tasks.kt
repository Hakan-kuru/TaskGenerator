package com.example.taskgenerator.domain.model

// Bir ana görev ve ona bağlı tüm alt görevler için domain modeli.
data class Main_with_sub_tasks(
    val mainTask: Main_task,
    val subTasks: List<Sub_task>
)
