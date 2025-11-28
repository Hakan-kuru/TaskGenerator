package com.example.taskgenerator.presentation.ui_states

// Ana ekrandaki kartların UI modeli (Main_screen.kt bu modeli kullanacak) :contentReference[oaicite:3]{index=3}
data class Main_task_ui_model(
    val id: Long,
    val title: String,
    val description: String,
    val taskType: Task_type_ui,

    val hasSubTasks: Boolean,
    val doneSubTaskCount: Int,
    val totalSubTaskCount: Int,

    val isDone: Boolean,
    val currentCount: Int?,
    val targetCount: Int?,

    // Zamanı "dakika" olarak göstermeyi istersen, Time tipinde currentCount/targetCount bunları temsil edecek.
    // İstersen bu alanları silebilirsin, şimdilik null bırakıyoruz.
    val currentMinutes: Int?,
    val targetMinutes: Int?,

    // UI'da gösterilecek tarih string'leri
    val startDateText: String?,
    val endDateText: String?
)

// UI tarafında taskType'ı temsil eden sealed class :contentReference[oaicite:4]{index=4}
sealed class Task_type_ui(val rawValue: String) {
    data object Done : Task_type_ui("done")
    data object Count : Task_type_ui("count")
    data object Time : Task_type_ui("time")
}

// Ana ekranın genel state'i
data class Main_screen_state(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val mainTasks: List<Main_task_ui_model> = emptyList()
)
