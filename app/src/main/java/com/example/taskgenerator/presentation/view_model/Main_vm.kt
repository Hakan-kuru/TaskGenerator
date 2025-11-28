package com.example.taskgenerator.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.usecase.Get_main_tasks_useCase
import com.example.taskgenerator.presentation.ui.screens.Main_screen_state
import com.example.taskgenerator.presentation.ui.screens.Main_task_ui_model
import com.example.taskgenerator.presentation.ui.screens.Task_type_ui

// @HiltViewModel: Hilt'in bu ViewModel'e constructor ile bağımlılık enjekte etmesini sağlayan anotasyon.
@HiltViewModel
class Main_vm @Inject constructor(
    // Bu usecase'leri kendi projendeki isimlerle değiştir.
    private val getMainTasksUseCase: Get_main_tasks_useCase,   // Tüm main task'ları getirir (Flow veya suspend)
    private val toggleMainTaskDoneUseCase: ToggleMainTaskDoneUseCase // isDone değiştirir
) : ViewModel() {

    // MutableStateFlow: UI state'ini tutan, Compose tarafından observe edilen reaktif akış.
    private val _state = MutableStateFlow(Main_screen_state())
    val state: StateFlow<Main_screen_state> = _state

    init {
        loadTasks()
    }

    fun refresh() {
        loadTasks()
    }

    fun toggleMainTaskDone(taskId: Long, done: Boolean) {
        viewModelScope.launch {
            try {
                toggleMainTaskDoneUseCase(
                    taskId = taskId,
                    isDone = done
                )
                // Başarılıysa listeyi tekrar yükleyelim
                loadTasks()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = e.message ?: "Görev durumu güncellenirken bir hata oluştu."
                    )
                }
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Burada senin GetMainTasksUseCase'inin dönüş tipine göre davranman lazım.
            // Ben Flow<List<MainTask>> döndüğünü varsaydım.
            getMainTasksUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Görevler yüklenirken bir hata oluştu."
                        )
                    }
                }
                .collectLatest { tasks ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            mainTasks = tasks.map { task -> task.toUiModel() }
                        )
                    }
                }
        }
    }

    // Buradaki MainTask, domain katmanındaki modelin. Property isimlerini kendi modeline göre uyarla.
    private fun Main_task.toUiModel(): Main_task_ui_model {
        // taskType alanını string tuttuğunu söylemiştin; onu UI enum'una map ediyoruz.
        val typeUi = when (taskType) {
            "Count" -> Task_type_ui.Count
            "Time" -> Task_type_ui.Time
            "Done" -> Task_type_ui.Done
            else -> Task_type_ui.Done
        }

        return Main_task_ui_model(
            id = id,
            title = title,
            description = description.orEmpty(),
            taskType = typeUi,
            hasSubTasks = ,
            doneSubTaskCount = ,
            totalSubTaskCount = ,
            isDone = isDone,
            currentCount = currentCount,
            targetCount = targetCount,
            currentMinutes = ,
            targetMinutes = ,
            // Tarihi domain’de nasıl tuttuğuna göre burayı düzenle
            startDateText = ,
            endDateText =
        )
    }
}
