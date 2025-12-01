package com.example.taskgenerator.presentation.ui_states


// MainTask ekleme ekranının UI state'i
data class Create_main_task_state(
    val title: String = "",
    val description: String = "",
    val taskType: String = "DONE", // varsayılan tür (yaptı/yapmadı)

    // COUNT / TIME tipi için hedef değer (sayı veya dakika)
    val targetCountText: String = "",

    val deadlineDateMillis: Long? = null,

    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val createdTaskId: Long? = null,

    val errorMessage: String? = null,
    val titleError: String? = null,
)