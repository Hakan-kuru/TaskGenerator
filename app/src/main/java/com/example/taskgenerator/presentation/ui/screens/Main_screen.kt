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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider // İLK KEZ kullanıyoruz: görev listesi ile alt paneli ayırmak için çizgi.
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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

// İLK KEZ kullanıyoruz: Üstteki iki buton için filtre tab enum'u.
private enum class TaskFilterTab {
    ACTIVE, // Şu anki tarihi geçmemiş görevler
    OVERDUE // Geçmiş görevler
}


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
    onSubTaskToggleDone: (subTaskId: Long, newValue: Boolean) -> Unit = { _, _ -> },
    onSubTaskCountChange: (subTaskId: Long, newCount: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {

    val expandedTaskIdState = rememberSaveable { mutableStateOf<Long?>(null) }

    // İki tab arasında geçiş için state
    val selectedTabState = rememberSaveable { mutableStateOf(TaskFilterTab.ACTIVE) }

    // Seçili main task'ın alt görevlerini altta ayrı bir panelde göstermek için state
    val selectedMainTaskIdState = rememberSaveable { mutableStateOf<Long?>(null) }

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
                    val selectedTab = selectedTabState.value

                    // Seçili tab'a göre gösterilecek liste
                    val currentList = when (selectedTab) {
                        TaskFilterTab.ACTIVE -> state.upcomingOrNoDeadlineMainTasks
                        TaskFilterTab.OVERDUE -> state.overdueMainTasks
                    }

                    // Seçili main task'ı tüm listeler içinden bul
                    val allTasks = state.upcomingOrNoDeadlineMainTasks + state.overdueMainTasks
                    val selectedMainTask =
                        allTasks.firstOrNull { it.id == selectedMainTaskIdState.value }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Üstte iki butonlu filtre bar
                        TaskFilterBar(
                            selectedTab = selectedTab,
                            onTabSelected = { newTab ->
                                selectedTabState.value = newTab
                                // Tab değişince seçili main task'ı sıfırlayabiliriz (isteğe bağlı)
                                selectedMainTaskIdState.value = null
                                expandedTaskIdState.value = null

                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Liste + alt paneli ayırmak için ağırlık veriyoruz
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            // Main task listesi (seçili tab'a göre)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(currentList) { task ->
                                    val isExpanded = expandedTaskIdState.value == task.id

                                    MainTaskItem(
                                        task = task,
                                        isExpanded = isExpanded,
                                        onCardClick = {
                                            // Kart tıklanınca hem seçili task'ı ayarlıyoruz
                                            selectedMainTaskIdState.value = task.id
                                            expandedTaskIdState.value =
                                                if (expandedTaskIdState.value == task.id) null else task.id

                                        },
                                        onToggleDone = { onToggleMainTaskDone(task.id) },
                                        onAddSubTaskClick = { onAddSubTaskClick(task.id) },
                                        onSubTaskToggleDone = { subTaskId, newValue ->
                                            onSubTaskToggleDone(subTaskId, newValue)
                                        },
                                        onSubTaskCountChange = { subTaskId, newCount ->
                                            onSubTaskCountChange(subTaskId, newCount)
                                        }
                                    )
                                }
                            }

                            Divider(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp))

                            // Alttaki ayrı panel: seçili main task'ın alt görevleri
                            SelectedMainTaskDetailPanel(
                                task = selectedMainTask,
                                onAddSubTaskClick = {
                                    selectedMainTask?.let { onAddSubTaskClick(it.id) }
                                },
                                onSubTaskToggleDone = onSubTaskToggleDone,
                                onSubTaskCountChange = onSubTaskCountChange
                            )
                        }
                    }
                }
            }
        }
    }
}

// İLK KEZ kullanıyoruz: Üstteki "Geçmiş görevler / Aktif görevler" geçiş barı.
@Composable
private fun TaskFilterBar(
    selectedTab: TaskFilterTab,
    onTabSelected: (TaskFilterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val activeSelected = selectedTab == TaskFilterTab.ACTIVE
        val overdueSelected = selectedTab == TaskFilterTab.OVERDUE

        Button(
            onClick = { onTabSelected(TaskFilterTab.ACTIVE) },
            modifier = Modifier.weight(1f),
            enabled = !activeSelected // seçiliyken pasif gösteriyoruz
        ) {
            Text("Aktif Görevler")
        }

        Button(
            onClick = { onTabSelected(TaskFilterTab.OVERDUE) },
            modifier = Modifier.weight(1f),
            enabled = !overdueSelected
        ) {
            Text("Geçmiş Görevler")
        }
    }
}

/**
 * Tek bir main task kartı.
 *
 * Kurallar:
 * - Alt görev varsa: kartta alt görevlerin toplam oranına göre progress bar gösterilir.
 * - Alt görev yok ve taskType Done ise: isDone butonu var.
 * - Count / Time için: genel progress bar + metin.
 *
 * Alt görevlerin detaylı kontrolü (one / count / time tipi için butonlar) alttaki ayrı panelde.
 */
@Composable
private fun MainTaskItem(
    task: Main_task_ui_model,
    isExpanded: Boolean,
    onSubTaskToggleDone: (Long, Boolean) -> Unit,
    onSubTaskCountChange: (Long, Int) -> Unit,
    onCardClick: () -> Unit,
    onToggleDone: () -> Unit,
    onAddSubTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Progress hesaplama
    var progress: Float? = null
    var progressText: String? = null

    when {
        task.hasSubTasks && task.totalSubTaskCount > 0 -> {
            val ratio =
                task.doneSubTaskCount.toFloat() / task.totalSubTaskCount.coerceAtLeast(1)
            progress = ratio.coerceIn(0f, 1f)
            progressText =
                "${task.doneSubTaskCount} / ${task.totalSubTaskCount} alt görev tamamlandı"
        }

        task.taskType is Task_type_ui.Count && task.targetCount != null -> {
            val current = task.currentCount ?: 0
            val target = task.targetCount
            val ratio = current.toFloat() / target.coerceAtLeast(1)
            progress = ratio.coerceIn(0f, 1f)
            progressText = "$current / $target"
        }

        task.taskType is Task_type_ui.Time && task.targetMinutes != null -> {
            val current = task.currentMinutes ?: 0
            val target = task.targetMinutes
            val ratio = current.toFloat() / target.coerceAtLeast(1)
            progress = ratio.coerceIn(0f, 1f)
            progressText = "$current / $target dk"
        }

        !task.hasSubTasks && task.taskType is Task_type_ui.Done -> {
            progress = if (task.isDone) 1f else 0f
            progressText = if (task.isDone) "Tamamlandı" else "Beklemede"
        }
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // ÜST SATIR: başlık + sağda ortalı bar (kapalıyken sadece burası görünür)
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

                if (progress != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.width(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // AÇILMIŞ HALİ
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tarihler
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

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (progressText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = progressText,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )

                        // Only-Done tipinde toggle butonu (subTask yokken)
                        if (!task.hasSubTasks && task.taskType is Task_type_ui.Done) {
                            IconButton(
                                onClick = { onToggleDone() }
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
                }

                Spacer(modifier = Modifier.height(8.dp))

                val subTasks = task.subTasks.orEmpty()

                if (subTasks.isEmpty()) {
                    Text(
                        text = "Bu görevin henüz alt görevi yok.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = onAddSubTaskClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Alt görev ekle")
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subTasks.forEach { subTask ->
                            SubTaskCard(
                                subTask = subTask,
                                onToggleDone = { newValue ->
                                    onSubTaskToggleDone(subTask.id, newValue)
                                },
                                onCountChange = { newCount ->
                                    onSubTaskCountChange(subTask.id, newCount)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * İLK KEZ kullanıyoruz: Ekranın altındaki ayrı panel.
 *
 * main task kartına tıklanınca, burada:
 * - Eğer alt görevleri varsa: her sub task için ayrı kart
 * - Eğer alt görev yoksa: bilgi mesajı
 */
@Composable
private fun SelectedMainTaskDetailPanel(
    task: Main_task_ui_model?,
    onAddSubTaskClick: () -> Unit,
    onSubTaskToggleDone: (Long, Boolean) -> Unit,
    onSubTaskCountChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (task == null) {
            Text(
                text = "Bir göreve tıklayınca alt görevleri burada göreceksin.",
                style = MaterialTheme.typography.bodySmall
            )
            return
        }

        Text(
            text = "Seçili görev: ${task.title}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val subTasks = task.subTasks.orEmpty()

        if (subTasks.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bu görevin henüz alt görevi yok.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = onAddSubTaskClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Alt görev ekle")
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subTasks.forEach { subTask ->

                    SubTaskCard(
                        subTask = subTask,
                        onToggleDone = { newValue ->
                            onSubTaskToggleDone(subTask.id, newValue)
                        },
                        onCountChange = { newCount ->

                            onSubTaskToggleDone(subTask.id,
                                newCount>= subTask.targetCount!!.toInt()
                            )
                            onSubTaskCountChange(subTask.id, newCount)
                        }
                    )
                }
            }
        }
    }
}

/**
 * İLK KEZ kullanıyoruz: Alt panelde görüntülenen sub task kartı.
 *
 * - Task_type_ui.One  -> on/off butonu ile isDone değişir.
 * - Task_type_ui.Count -> bar + sol/sağında - / + butonları.
 * - Task_type_ui.Time  -> Saat ikonu, şimdilik sadece gösterim (onClick yok).
 */
@Composable
private fun SubTaskCard(
    subTask: Sub_task_ui_model,
    onToggleDone: (Boolean) -> Unit,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subTask.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                when (subTask.taskType) {
                    is Task_type_ui.Done -> {
                        IconButton(
                            onClick = { onToggleDone(!subTask.isDone) }
                        ) {
                            Icon(
                                imageVector = if (subTask.isDone) {
                                    Icons.Filled.CheckCircle
                                } else {
                                    Icons.Outlined.CheckCircle
                                },
                                contentDescription = "Alt görev tamamlandı mı?",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    is Task_type_ui.Count -> {
                        // Count tipi için artı / eksi butonları
                        val current = subTask.currentCount ?: 0
                        val target = subTask.targetCount ?: 0

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val newValue = (current - 1).coerceAtLeast(0)
                                    onCountChange(newValue)
                                }
                            ) {
                                Text("-")
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Column(
                                modifier = Modifier.width(120.dp)
                            ) {
                                val ratio =
                                    if (target > 0) current.toFloat() / target else 0f
                                LinearProgressIndicator(
                                    progress = { ratio.coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$current / $target",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    val newValue = current + 1
                                    onCountChange(newValue)
                                }
                            ) {
                                Text("+")
                            }
                        }
                    }

                    is Task_type_ui.Time -> {
                        // Şimdilik sadece saat ikonu + metin, onClick yok.
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Zamanlayıcı",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val current = subTask.currentMinutes ?: 0
                            val target = subTask.targetMinutes ?: 0
                            Text(
                                text = "$current / $target dk",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            if (subTask.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subTask.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
