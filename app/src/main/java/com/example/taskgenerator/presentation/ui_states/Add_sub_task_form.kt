package com.example.taskgenerator.presentation.ui_states

// Add_Sub_task_screen için form modeli :contentReference[oaicite:6]{index=6}
data class Add_sub_task_form(
    val parentTaskId: Long,
    val title: String,
    val description: String,
    val taskType: Task_type_ui,
    val targetCount: Int?,
    val targetMinutes: Int?
)