package com.example.taskgenerator.presentation.uiModel

// Ana ekrandaki kartların UI modeli (Main_screen.kt bu modeli kullanacak) :contentReference[oaicite:3]{index=3}
data class Main_task_ui_model(
    val id: Long,
    val title: String,
    val description: String,
    val taskType: Task_type_ui,

    val hasSubTasks: Boolean,
    val subTasks: List<Sub_task_ui_model>?,
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
