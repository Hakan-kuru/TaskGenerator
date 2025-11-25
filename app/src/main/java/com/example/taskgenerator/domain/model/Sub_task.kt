package com.example.taskgenerator.domain.model

// Domain katmanında kullanılacak alt görev modeli.
data class Sub_task(
    val id: Int,
    val mainTaskId: Long,         // Bu alt görevin bağlı olduğu ana görevin id'si.
    val title: String,
    val description: String? = null,
    val taskType: String,         // "done", "count", "time"
    val targetCount: Int? = null,
    val currentCount: Int? = null,
    val isDone: Boolean = false
)
