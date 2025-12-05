package com.example.taskgenerator.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskgenerator.domain.usecase.Get_main_task_with_sub_tasks_useCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import com.example.taskgenerator.domain.usecase.Toggle_main_task_done_usecase
import com.example.taskgenerator.domain.usecase.Update_sub_task_progress_useCase
import com.example.taskgenerator.presentation.ui.screens.Main_screen_state
import com.example.taskgenerator.utils.toUiModel

// @HiltViewModel: Hilt'in bu ViewModel'e constructor ile bağımlılık enjekte etmesini sağlayan anotasyon.
@HiltViewModel
class Main_vm @Inject constructor(
    // Bu usecase'leri kendi projendeki isimlerle değiştir.
    private val getMainTasksGroupedByDeadlineUseCase: Get_main_task_with_sub_tasks_useCase,
    private val toggleMainTaskDoneUseCase: Toggle_main_task_done_usecase, // isDone değiştirir
    private val update_sub_task_progress_useCase:  Update_sub_task_progress_useCase
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

    fun toggleMainTaskDone(mainId: Long) {
        viewModelScope.launch {
            try {
                toggleMainTaskDoneUseCase(
                    mainTaskId = mainId
                )
                Log.i("toggle", "toge başarılı")
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

    fun onSubTaskCountChange(subTaskId: Long, newCount: Int) {
        viewModelScope.launch {
            try {
                update_sub_task_progress_useCase(
                    subTaskId = subTaskId,
                    isDone = null,
                    currentCount = newCount
                )
                refresh()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Alt görev sayısı güncellenemedi."
                )
            }
        }
    }

    fun onSubTaskToggleDone(subTaskId: Long, newValue: Boolean) {
        viewModelScope.launch {
            try {
                update_sub_task_progress_useCase(
                    subTaskId = subTaskId,
                    isDone = newValue,
                    currentCount = null
                )
                // Sub task değişince ekrandaki değerlerin de güncellenmesi için yeniden yükle
                refresh()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Alt görev güncellenemedi."
                )
            }
        }
    }


    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Burada artık deadline'a göre gruplayan usecase'i kullanıyoruz.
            getMainTasksGroupedByDeadlineUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Görevler yüklenirken bir hata oluştu."
                        )
                    }
                }
                .collectLatest { groupedTasks ->
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            // İLK KEZ kullanıyoruz: domain Main_with_sub_tasks -> UI model dönüşümü için toUiModel() mapper
                            overdueMainTasks = groupedTasks.overdue.map { it.toUiModel() },
                            upcomingOrNoDeadlineMainTasks = groupedTasks.upcomingOrNoDeadline.map { it.toUiModel() }
                        )
                    }
                }
        }
    }
    // Buradaki MainTask, domain katmanındaki modelin. Property isimlerini kendi modeline göre uyarla.
}
