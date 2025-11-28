package com.example.taskgenerator.presentation.ui_states

data class Sub_task_ui_model(
    val id: Long,
    val mainTaskId: Long,
    val title: String,
    val description: String,
    val taskType: Task_type_ui,
    val isDone: Boolean,
    val currentCount: Int?,
    val targetCount: Int?,
    val currentMinutes: Int?,
    val targetMinutes: Int?
)