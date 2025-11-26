package com.example.taskgenerator.presentation.ui_states


// Her alt görev satırının UI'da temsil edilecek hâli
data class SubTaskUiState(
    val id: Long,
    val title: String,
    val description: String?,
    val isDone: Boolean,
    val progressLabel: String?,   // Örn: "3 / 5", "20 / 30 dk"
    val progressPercent: Int?,    // 0..100, sadece time/count için
    val showToggle: Boolean       // Sadece basit done tiplerinde true olacak
)

// Ana ekrandaki her MainTask kartının UI hâli
data class MainTaskListItemUiState(
    val id: Long,
    val title: String,
    val description: String?,
    val periodText: String?,      // "12.11.2025 - 30.11.2025" gibi (şimdilik string)
    val taskType: String,
    val hasSubTasks: Boolean,
    val isExpanded: Boolean,

    val isDone: Boolean,          // UI'da tamamlandı mı göstermek için
    val showIsDoneToggle: Boolean, // Alt görev yok + taskType Done ise true
    val showProgressBar: Boolean, // Hangi durumda bar gösterileceğini ViewModel hesaplıyor
    val progressPercent: Int,     // 0..100
    val progressLabel: String?,   // "3 / 5 alt görev", "20 / 30 dk" gibi

    val subTasks: List<SubTaskUiState>
)

// Ana listenin genel state'i
data class MainTaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<MainTaskListItemUiState> = emptyList(),
    val errorMessage: String? = null
)