package com.example.taskgenerator.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.usecase.Create_main_task_useCase
import com.example.taskgenerator.presentation.uiModel.Task_type_ui
import com.example.taskgenerator.presentation.ui_states.Create_main_task_state
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @HiltViewModel: İLK KEZ kullanıyoruz: Hilt'in bu ViewModel'e constructor ile bağımlılık enjekte etmesini sağlar.
@HiltViewModel
class Create_main_task_vm @Inject constructor(
    private val createMainTaskUseCase: Create_main_task_useCase
) : ViewModel() {

    private val _state = MutableStateFlow(Create_main_task_state())
    val state: StateFlow<Create_main_task_state> = _state

    fun onTitleChange(newTitle: String) {
        _state.update {
            it.copy(
                title = newTitle,
                titleError = null
            )
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _state.update { it.copy(description = newDescription) }
    }

    fun onDeadlineSelected(millis: Long) {
        _state.update {
            it.copy(deadlineDateMillis = millis)
        }
    }

    fun onTaskTypeChange(newType: Task_type_ui) {
        _state.update { current ->
            current.copy(
                taskType = newType,
                // tip incidence hedef alanını sıfırlayalım
                targetCountText = ""
            )
        }
    }

    fun onTargetCountChange(newTarget: String) {
        _state.update { it.copy(targetCountText = newTarget) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null, titleError = null) }
    }

    fun onSaveClicked() {
        val current = state.value

        // basit validasyon
        if (current.title.isBlank()) {
            _state.update {
                it.copy(
                    titleError = "Başlık boş olamaz.",
                    errorMessage = "Lütfen görev başlığını gir."
                )
            }
            return
        }

        // COUNT / TIME tipi için hedef sayı validasyonu
        val targetCount: Int? = when (current.taskType) {
            Task_type_ui.Count, Task_type_ui.Time -> {
                if (current.targetCountText.isBlank()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Bu görev tipi için hedef değer girmelisin."
                        )
                    }
                    return
                }
                val parsed = current.targetCountText.toIntOrNull()
                if (parsed == null || parsed <= 0) {
                    _state.update {
                        it.copy(
                            errorMessage = "Hedef değer pozitif bir sayı olmalı."
                        )
                    }
                    return
                }
                parsed
            }

            else -> null // DONE tipi için gerek yok
        }

        val nowMillis = System.currentTimeMillis()

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                // Burada Main_task id'si null: ekleme için kullanıyoruz.
                val newTask = Main_task(
                    id = null,
                    title = current.title.trim(),
                    description = current.description.trim().ifBlank { null },
                    taskType = current.taskType.toString(), // "DONE", "COUNT", "TIME" gibi
                    isDone = false,
                    targetCount = targetCount,
                    currentCount = if (targetCount != null) 0 else null,
                    createdAt = nowMillis,
                    updatedAt = current.deadlineDateMillis!!.toLong()
                )

                val newId = createMainTaskUseCase(newTask)

                _state.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        createdTaskId = newId
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Görev oluşturulurken bir hata oluştu."
                    )
                }
            }
        }
    }
}
