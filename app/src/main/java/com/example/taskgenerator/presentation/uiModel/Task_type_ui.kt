package com.example.taskgenerator.presentation.uiModel

// UI tarafında taskType'ı temsil eden sealed class :contentReference[oaicite:4]{index=4}
sealed class Task_type_ui(val rawValue: String) {
    data object Done : Task_type_ui("done")
    data object Count : Task_type_ui("count")
    data object Time : Task_type_ui("time")
}
