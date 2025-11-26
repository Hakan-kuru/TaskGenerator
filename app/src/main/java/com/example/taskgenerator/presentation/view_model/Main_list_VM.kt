package com.example.taskgenerator.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.domain.model.Sub_task
import com.example.taskgenerator.domain.usecase.Delete_sub_task_useCase
import com.example.taskgenerator.domain.usecase.Get_main_task_with_sub_tasks_useCase
import com.example.taskgenerator.domain.usecase.Get_main_tasks_useCase
import com.example.taskgenerator.domain.usecase.Update_main_task_useCase
import com.example.taskgenerator.domain.usecase.Update_sub_task_progress_useCase
import com.example.taskgenerator.presentation.ui_states.MainTaskListItemUiState
import com.example.taskgenerator.presentation.ui_states.MainTaskListUiState
import com.example.taskgenerator.presentation.ui_states.SubTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.find
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel // Hilt'in bu ViewModel'i otomatik olarak sağlayabilmesi için
class Main_list_VM @Inject constructor(
    private val get_main_tasks_useCase: Get_main_tasks_useCase ,
    private val get_main_task_with_sub_tasks_useCase: Get_main_task_with_sub_tasks_useCase,
    private val update_main_task_useCase: Update_main_task_useCase,             // <-- eksikse yazman lazım
    private val update_sub_task_progress_useCase: Update_sub_task_progress_useCase,
    private val delete_sub_task_useCase: Delete_sub_task_useCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainTaskListUiState(isLoading = true))
    val uiState: StateFlow<MainTaskListUiState> = _uiState

    private var cachedMainTasks: List<Main_task> = emptyList()
    private var cachedSubTasks: MutableMap<Long, List<Sub_task>> = mutableMapOf()
    private var expandedTaskIds: MutableSet<Long> = mutableSetOf()

    init {
        observeMainTasks()
    }

    private fun observeMainTasks() {
        viewModelScope.launch {
            get_main_tasks_useCase() // Flow<List<Main_task>>
                .onEach { tasks ->
                    cachedMainTasks = tasks
                    rebuildUi()
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Bir hata oluştu"
                    )
                }
                .collect { /* Flow zaten onEach içinde işleniyor */ }
        }
    }

    fun onToggleMainTaskExpanded(taskId: Long) {
        if (expandedTaskIds.contains(taskId)) {
            expandedTaskIds.remove(taskId)
        } else {
            expandedTaskIds.add(taskId)
            // İlk defa açılıyorsa alt görevleri çek
            loadSubTasks(taskId)
        }
        rebuildUi()
    }

    fun onToggleMainTaskDone(taskId: Long) {
        viewModelScope.launch {
            val mainTask = cachedMainTasks.find { it.id == taskId } ?: return@launch

            val subList = cachedSubTasks[taskId].orEmpty()
            val hasSubTasks = subList.isNotEmpty()
            val typeLower = mainTask.taskType.lowercase()

            // Kural: alt görev varsa YA DA taskType "done" değilse toggle yok.
            if (hasSubTasks || typeLower != "done") return@launch

            val updated = mainTask.copy(isDone = !mainTask.isDone)
            // Bunu kullanmak için Update_main_task_useCase'i oluşturmuş olman gerekiyor.
            update_main_task_useCase(updated)
        }
    }

    fun onToggleSubTaskDone(mainTaskId: Long, subTaskId: Long) {
        viewModelScope.launch {
            val subList = cachedSubTasks[mainTaskId].orEmpty()
            val target = subList.find { it.id == subTaskId } ?: return@launch

            val newIsDone = !target.isDone
            update_sub_task_progress_useCase(
                subTaskId = subTaskId,
                isDone = newIsDone,
                currentCount = null
            )

            // Güncel alt görevleri tekrar çekip oranları güncelle
            loadSubTasks(mainTaskId)
        }
    }

    fun onDeleteSubTask(mainTaskId: Long, subTaskId: Long) {
        viewModelScope.launch {
            delete_sub_task_useCase(subTaskId)
            loadSubTasks(mainTaskId)
        }
    }

    private fun loadSubTasks(mainTaskId: Long) {
        viewModelScope.launch {
            // Bu useCase Main_with_sub_tasks? döndürüyor
            val result: Main_with_sub_tasks? =
                get_main_task_with_sub_tasks_useCase(mainTaskId).first()

            val subList = result?.subTasks ?: emptyList()
            cachedSubTasks[mainTaskId] = subList
            rebuildUi()
        }
    }

    private fun rebuildUi() {
        val items = cachedMainTasks.map { mainTask ->
            val subList = cachedSubTasks[mainTask.id].orEmpty()
            val isExpanded = expandedTaskIds.contains(mainTask.id)

            buildMainTaskUi(
                mainTask = mainTask,
                subTasks = subList,
                isExpanded = isExpanded
            )
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            tasks = items
        )
    }

    private fun buildMainTaskUi(
        mainTask: Main_task,
        subTasks: List<Sub_task>,
        isExpanded: Boolean
    ): MainTaskListItemUiState {
        val typeLower = mainTask.taskType.lowercase()
        val hasSubTasks = subTasks.isNotEmpty()

        val progress = computeMainTaskProgress(mainTask, subTasks, typeLower)

        val subTaskUiStates = if (isExpanded && hasSubTasks) {
            subTasks.map { buildSubTaskUi(it) }
        } else {
            emptyList()
        }

        return MainTaskListItemUiState(
            id = mainTask.id,
            title = mainTask.title,
            description = mainTask.description,
            periodText = null, // Main_task modeline tarih alanları eklediğinde burayı doldur
            taskType = mainTask.taskType,
            hasSubTasks = hasSubTasks,
            isExpanded = isExpanded,
            isDone = progress.isDone,
            showIsDoneToggle = progress.showToggle,
            showProgressBar = progress.showProgressBar,
            progressPercent = progress.progressPercent,
            progressLabel = progress.progressLabel,
            subTasks = subTaskUiStates
        )
    }

    private fun computeMainTaskProgress(
        mainTask: Main_task,
        subTasks: List<Sub_task>,
        typeLower: String
    ): MainTaskProgressResult {
        val hasSubTasks = subTasks.isNotEmpty()

        // 1) Alt görev varsa: isDone ve oran SubTask'lerden gelir, toggleda yok
        if (hasSubTasks) {
            val total = subTasks.size
            val doneCount = subTasks.count { it.isDone }
            val percent = if (total > 0) (doneCount * 100 / total) else 0
            val isDone = total > 0 && doneCount == total
            val label = "$doneCount / $total alt görev tamamlandı (%$percent)"

            return MainTaskProgressResult(
                isDone = isDone,
                showToggle = false,        // alt görev varsa mainTask isDone butonu yok
                showProgressBar = total > 0,
                progressPercent = percent,
                progressLabel = label
            )
        }

        // 2) Alt görev yok + Done tipi: sadece toggle, progress bar yok
        if (typeLower == "done") {
            return MainTaskProgressResult(
                isDone = mainTask.isDone,
                showToggle = true,
                showProgressBar = false,
                progressPercent = 0,
                progressLabel = null
            )
        }

        // 3) Alt görev yok + time/count: sayısal progress bar, toggle yok
        val target = mainTask.targetCount ?: 0
        val current = mainTask.currentCount ?: 1
        val percent = if (target > 0) {
            (current * 100 / target).coerceIn(0, 100)
        } else 0

        val unitLabel = if (typeLower == "time") "dk" else ""
        val label = if (target > 0) {
            if (unitLabel.isNotEmpty()) "$current / $target $unitLabel"
            else "$current / $target"
        } else null

        return MainTaskProgressResult(
            isDone = mainTask.isDone,
            showToggle = false,
            showProgressBar = target > 0,
            progressPercent = percent,
            progressLabel = label
        )
    }

    private fun buildSubTaskUi(subTask: Sub_task): SubTaskUiState {
        val typeLower = subTask.taskType.lowercase()

        val target = subTask.targetCount ?: 0
        val current = subTask.currentCount ?: 1

        val (progressLabel, progressPercent) = when (typeLower) {
            "time" -> {
                val percent = if (target > 0) (current * 100 / target).coerceIn(0, 100) else 0
                val label = if (target > 0) "$current / $target dk" else null
                label to percent
            }
            "count" -> {
                val percent = if (target > 0) (current * 100 / target).coerceIn(0, 100) else 0
                val label = if (target > 0) "$current / $target" else null
                label to percent
            }
            else -> null to null
        }

        val showToggle = typeLower == "done"

        return SubTaskUiState(
            id = subTask.id,
            title = subTask.title,
            description = subTask.description,
            isDone = subTask.isDone,
            progressLabel = progressLabel,
            progressPercent = progressPercent,
            showToggle = showToggle
        )
    }

    private data class MainTaskProgressResult(
        val isDone: Boolean,
        val showToggle: Boolean,
        val showProgressBar: Boolean,
        val progressPercent: Int,
        val progressLabel: String?
    )
}