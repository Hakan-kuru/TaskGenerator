package com.example.taskgenerator.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskgenerator.domain.usecase.Create_sub_task_useCase
import com.example.taskgenerator.presentation.uiModel.Task_type_ui
import com.example.taskgenerator.presentation.ui_states.Add_sub_task_ui_state
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Add_sub_task_vm @Inject constructor(
    private val createSubTaskUseCase: Create_sub_task_useCase // domain'e giden tek kapı
) : ViewModel() {

    private val _state = MutableStateFlow(Add_sub_task_ui_state())
    val state = _state.asStateFlow()

    /**
     * NavGraph'ten gelen ana görev bilgilerini ilk girişte set ediyorsun.
     * Task_nav_rot içindeki LaunchedEffect burayı çağırıyor.
     */
    fun setParentTask(parentTaskId: Long, parentTaskTitle: String?) {
        _state.update {
            it.copy(
                parentTaskId = parentTaskId,
                parentTaskTitle = parentTaskTitle
            )
        }
    }

    fun onTitleChange(newTitle: String) {
        _state.update {
            it.copy(
                title = newTitle,
                titleError = if (newTitle.isBlank()) "Başlık boş olamaz" else null,
                errorMessage = null
            )
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _state.update {
            it.copy(
                description = newDescription,
                errorMessage = null
            )
        }
    }

    fun onTaskTypeChange(type: Task_type_ui) {
        _state.update {
            it.copy(
                taskType = type,
                // TaskType değişince eski hedef hatalarını temizleyelim
                targetError = null,
                errorMessage = null,
                // Tip değişince eski input'u temizlemek istersen:
                // targetCountText = if (type is Task_type_ui.Count) it.targetCountText else "",
                // targetMinutesText = if (type is Task_type_ui.Time) it.targetMinutesText else ""
            )
        }
    }

    fun onTargetCountChange(text: String) {
        _state.update {
            it.copy(
                targetCountText = text,
                targetError = null,
                errorMessage = null
            )
        }
    }

    fun onTargetMinutesChange(text: String) {
        _state.update {
            it.copy(
                targetMinutesText = text,
                targetError = null,
                errorMessage = null
            )
        }
    }

    fun onSaveClicked() {
        val current = state.value

        // 1) ParentTask kontrolü
        if (current.parentTaskId == null) {
            _state.update {
                it.copy(errorMessage = "Ana görev bilgisi bulunamadı.")
            }
            return
        }

        // 2) Başlık validasyonu
        val titleTrimmed = current.title.trim()
        if (titleTrimmed.isEmpty()) {
            _state.update {
                it.copy(titleError = "Başlık boş olamaz")
            }
            return
        }

        // 3) TaskType'a göre hedef validasyonu
        var targetCount: Int? = null
        var targetMinutes: Int? = null

        when (current.taskType) {
            is Task_type_ui.Count -> {
                if (current.targetCountText.isBlank()) {
                    _state.update {
                        it.copy(targetError = "Hedef sayı zorunlu")
                    }
                    return
                }
                val parsed = current.targetCountText.toIntOrNull()
                if (parsed == null || parsed <= 0) {
                    _state.update {
                        it.copy(targetError = "Geçerli bir pozitif sayı gir")
                    }
                    return
                }
                targetCount = parsed
            }

            is Task_type_ui.Time -> {
                if (current.targetMinutesText.isBlank()) {
                    _state.update {
                        it.copy(targetError = "Hedef süre zorunlu")
                    }
                    return
                }
                val parsed = current.targetMinutesText.toIntOrNull()
                if (parsed == null || parsed <= 0) {
                    _state.update {
                        it.copy(targetError = "Geçerli bir pozitif dakika gir")
                    }
                    return
                }
                targetMinutes = parsed
            }

            is Task_type_ui.Done -> {
                // Done tipi: hedef yok, hepsi null.
            }
        }

        // 4) Kaydetme sürecine giriyoruz
        _state.update {
            it.copy(
                isSaving = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                // Domain katmanına yalnızca primitive data ve rawValue gönderiyoruz.
                createSubTaskUseCase(
                    mainTaskId = current.parentTaskId,
                    title = titleTrimmed,
                    description = current.description.takeIf { it.isNotBlank() },
                    taskType = current.taskType.rawValue,
                    targetCount = if (
                        current.taskType == Task_type_ui.Done) null
                    else if (targetMinutes ==null) targetCount
                    else targetMinutes,
                )

                _state.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true // Screen içindeki LaunchedEffect onSaved() çağıracak.
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Alt görev kaydedilirken bir hata oluştu."
                    )
                }
            }
        }
    }
}
