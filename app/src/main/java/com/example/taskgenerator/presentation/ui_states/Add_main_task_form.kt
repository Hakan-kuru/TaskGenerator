package com.example.taskgenerator.presentation.ui_states

data class Add_main_task_form(
    val title: String,
    val description: String,
    val taskType: Task_type_ui,
    val targetCount: Int?,
    val targetMinutes: Int?,
    val startDate: String?,
    val endDate: String?
)