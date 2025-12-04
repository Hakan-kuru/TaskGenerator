package com.example.taskgenerator.presentation.ui_states

import com.example.taskgenerator.presentation.uiModel.Task_type_ui

class Add_sub_task_ui_state (

    val parentTaskId: Long = 0L,
    val parentTaskTitle: String? =null,
    val title: String = "",
    val description: String = "",

    val taskType: Task_type_ui = Task_type_ui.Done, // varsayılan tür (yaptı/yapmadı

    val deadlineDateMillis: Long = 0L,


    val targetCountText: String = "",
    val targetMinutesText: String = "",
    val targetError: String? = null,

    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val createdTaskId: Long? = null,

    val errorMessage: String? = null,
    val titleError: String? = null,
)