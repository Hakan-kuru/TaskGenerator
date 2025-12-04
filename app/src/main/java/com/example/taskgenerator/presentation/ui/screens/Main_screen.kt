package com.example.taskgenerator.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskgenerator.presentation.uiModel.Main_task_ui_model
import com.example.taskgenerator.presentation.uiModel.Sub_task_ui_model
import com.example.taskgenerator.presentation.uiModel.Task_type_ui

// Ana ekranın ihtiyaç duyduğu state
data class Main_screen_state(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val upcomingOrNoDeadlineMainTasks: List<Main_task_ui_model> = emptyList(),
    val overdueMainTasks: List<Main_task_ui_model> = emptyList()
)

/**
 * Ana Task listesi ekranı.
 *
 * ViewModel tarafında StateFlow/LiveData vb. ile bu state'i üretip buraya verebilirsin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: Main_screen_state,
    onRefresh: () -> Unit,
    onMainTaskClick: (Long) -> Unit,
    onToggleMainTaskDone: (taskId: Long) -> Unit,
    onAddSubTaskClick: (mainTaskId: Long) -> Unit,
    onAddMainTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedTaskIdsState = rememberSaveable { mutableStateOf(setOf<Long>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Generator") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMainTaskClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yeni görev ekle"
                )
            }
        },
        modifier = modifier
    ) { padding ->

        val isEmpty =
            state.overdueMainTasks.isEmpty() && state.upcomingOrNoDeadlineMainTasks.isEmpty()

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) {
                            Text("Tekrar dene")
                        }
                    }
                }

                isEmpty -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Henüz hiç görev eklemedin.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sağ alttaki + butonuna basarak ilk görevi oluşturabilirsin.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Önce tarihi geçmiş görevler
                        if (state.overdueMainTasks.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Tarihi Geçmiş Görevler",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            items(state.overdueMainTasks) { task ->
                                val isExpanded =
                                    expandedTaskIdsState.value.contains(task.id)

                                MainTaskItem(
                                    task = task,
                                    isExpanded = isExpanded,
                                    onCardClick = {
                                        val current = expandedTaskIdsState.value
                                        expandedTaskIdsState.value =
                                            if (isExpanded) current - task.id else current + task.id

                                        // İstersen burada detay ekrana gitmek için de çağırabilirsin:
                                        // onMainTaskClick(task.id)
                                    },
                                    onClick = { onMainTaskClick(task.id) },
                                    onToggleDone = { onToggleMainTaskDone(task.id) },
                                    onAddSubTaskClick = { onAddSubTaskClick(task.id) }
                                )
                            }
                        }

                        // Sonra diğerleri
                        if (state.upcomingOrNoDeadlineMainTasks.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Diğer Görevler",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            items(state.upcomingOrNoDeadlineMainTasks) { task ->
                                val isExpanded =
                                    expandedTaskIdsState.value.contains(task.id)

                                MainTaskItem(
                                    task = task,
                                    isExpanded = isExpanded,
                                    onCardClick = {
                                        val current = expandedTaskIdsState.value
                                        expandedTaskIdsState.value =
                                            if (isExpanded) current - task.id else current + task.id
                                    },
                                    onClick = { onMainTaskClick(task.id) },
                                    onToggleDone = { onToggleMainTaskDone(task.id) },
                                    onAddSubTaskClick = { onAddSubTaskClick(task.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tek bir main task kartı.
 *
 * Kurallar:
 * - Alt görev varsa: isDone butonu yok, başarı oranı/progress bar var.
 * - Alt görev yok ve taskType Done ise: sadece isDone butonu var, oran yok.
 * - Count / Time için: mevcut değere göre progress bar + oran text.
 */
@Composable
private fun MainTaskItem(
    task: Main_task_ui_model,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onClick: () -> Unit,
    onToggleDone: (Boolean) -> Unit,
    onAddSubTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal =  12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Alt görev yoksa ve TaskType Done ise isDone toggle göster
                if (!task.hasSubTasks && task.taskType is Task_type_ui.Done) {
                    IconButton(
                        onClick = { onToggleDone(!task.isDone) }
                    ) {
                        Icon(
                            imageVector = if (task.isDone) {
                                Icons.Filled.CheckCircle
                            } else {
                                Icons.Outlined.CheckCircle
                            },
                            contentDescription = "Görev tamamlandı mı?"
                        )
                    }
                }
            }

            if (!isExpanded) return@Card

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                task.startDateText?.let {
                    Text(
                        text = "Başlangıç: $it",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                task.endDateText?.let {
                    Text(
                        text = "Bitiş: $it",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                // Alt görev varsa: başarı oranı
                task.hasSubTasks && task.totalSubTaskCount > 0 -> {
                    val ratio =
                        task.doneSubTaskCount.toFloat() / task.totalSubTaskCount.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${task.doneSubTaskCount} / ${task.totalSubTaskCount} alt görev tamamlandı",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Count tipinde görev
                task.taskType is Task_type_ui.Count && task.targetCount != null -> {
                    val current = task.currentCount ?: 0
                    val target = task.targetCount
                    val ratio = current.toFloat() / target.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$current / $target",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Time tipinde görev (dakika)
                task.taskType is Task_type_ui.Time && task.targetMinutes != null -> {
                    val current = task.currentMinutes ?: 0
                    val target = task.targetMinutes
                    val ratio = current.toFloat() / target.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$current / $target dk",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            val subTasks = task.subTasks.orEmpty()
            if (subTasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    subTasks.forEach { subTask ->
                        SubTaskRow(subTask = subTask)
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onAddSubTaskClick) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Alt görev ekle")
                }
            }
        }
    }
}

@Composable
private fun SubTaskRow(
    subTask: Sub_task_ui_model,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (subTask.isDone) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = subTask.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subTask.description.isNotBlank()) {
                Text(
                    text = subTask.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}