package com.example.taskgenerator.presentation.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskgenerator.presentation.ui.theme.AppTheme
import com.example.taskgenerator.presentation.uiModel.Main_task_ui_model
import com.example.taskgenerator.presentation.uiModel.Sub_task_ui_model
import com.example.taskgenerator.presentation.uiModel.Task_type_ui
// kullanıyoruz: swipe ile silme/düzenleme için Material (M2) bileşenleri.
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState

import androidx.compose.material3.AlertDialog

// Ana ekranın ihtiyaç duyduğu state
data class Main_screen_state(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val upcomingOrNoDeadlineMainTasks: List<Main_task_ui_model> = emptyList(),
    val overdueMainTasks: List<Main_task_ui_model> = emptyList()
)

// kullanıyoruz: Üstteki iki buton için filtre tab enum'u.
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
    //  swipe ile silme/düzenleme için callback'ler
    onMainTaskDelete: (Long) -> Unit = {},   // main task sil (arkada tüm sub task'ları da silersin)
    onSubTaskDelete: (Long) -> Unit = {},    // sub task sil
    onSubTaskClick: (Long) -> Unit = {},     // sub task düzenleme ekranına git
    modifier: Modifier = Modifier
) {

    val expandedTaskIdState = rememberSaveable { mutableStateOf<Long?>(null) }

    // İki tab arasında geçiş için state
    val selectedTabState = rememberSaveable { mutableStateOf(TaskFilterTab.ACTIVE) }

    // Seçili main task'ın alt görevlerini altta ayrı bir panelde göstermek için state
    val selectedMainTaskIdState = rememberSaveable { mutableStateOf<Long?>(null) }

    val mainTaskToDeleteIdState = rememberSaveable { mutableStateOf<Long?>(null) }
    val subTaskToDeleteIdState = rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Task Generator",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMainTaskClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yeni görev ekle",
                    tint = MaterialTheme.colorScheme.onPrimary
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
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
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
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Tekrar dene",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
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
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sağ alttaki + butonuna basarak ilk görevi oluşturabilirsin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
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

                                    SwipeableMainTaskItem(
                                        task = task,
                                        isExpanded = isExpanded,
                                        onCardClick = {
                                            val isCurrentlyExpanded = expandedTaskIdState.value == task.id

                                            if (isCurrentlyExpanded) {
                                                // Kart zaten açıksa: kapat + alttaki paneli temizle
                                                expandedTaskIdState.value = null
                                                selectedMainTaskIdState.value = null
                                            } else {
                                                // Yeni kartı aç: hem expand, hem alt panelde bunu seç
                                                expandedTaskIdState.value = task.id
                                                selectedMainTaskIdState.value = task.id
                                            }
                                        },
                                        onToggleDone = { onToggleMainTaskDone(task.id) },
                                        onAddSubTaskClick = { onAddSubTaskClick(task.id) },
                                        onSubTaskToggleDone = { subTaskId, newValue ->
                                            onSubTaskToggleDone(subTaskId, newValue)
                                        },
                                        onSubTaskCountChange = { subTaskId, newCount ->
                                            onSubTaskCountChange(subTaskId, newCount)
                                        },
                                        onEdit = { id -> onMainTaskClick(id) },                  // SAĞA kaydırınca düzenle
                                        onDeleteRequest = { id -> mainTaskToDeleteIdState.value = id } // SOLA kaydırınca dialog aç
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
                                onSubTaskCountChange = onSubTaskCountChange,
                                onSubTaskClick = onSubTaskClick,
                                onSubTaskDeleteRequest = { id -> subTaskToDeleteIdState.value = id }
                            )

                        }
                    }
                }
            }
        }
    }

    // Main task silme dialogu
    if (mainTaskToDeleteIdState.value != null) {
        AlertDialog(
            onDismissRequest = { mainTaskToDeleteIdState.value = null },
            title = {
                Text(text = "Görevi sil")
            },
            text = {
                Text(
                    text = "Bu ana görevi ve bağlı tüm alt görevleri silmek istediğine emin misin?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mainTaskToDeleteIdState.value?.let { onMainTaskDelete(it) }
                        mainTaskToDeleteIdState.value = null
                    }
                ) {
                    Text(text = "Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mainTaskToDeleteIdState.value = null }
                ) {
                    Text(text = "Vazgeç")
                }
            }
        )
    }

    // Sub task silme dialogu
    if (subTaskToDeleteIdState.value != null) {
        AlertDialog(
            onDismissRequest = { subTaskToDeleteIdState.value = null },
            title = {
                Text(text = "Alt görevi sil")
            },
            text = {
                Text(
                    text = "Bu alt görevi silmek istediğine emin misin?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        subTaskToDeleteIdState.value?.let { onSubTaskDelete(it) }
                        subTaskToDeleteIdState.value = null
                    }
                ) {
                    Text(text = "Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { subTaskToDeleteIdState.value = null }
                ) {
                    Text(text = "Vazgeç")
                }
            }
        )
    }

}

// Üstteki "Geçmiş görevler / Aktif görevler" geçiş barı.
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
            enabled = !activeSelected,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (activeSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Aktif Görevler",
                color = if (activeSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onPrimary
            )
        }

        Button(
            onClick = { onTabSelected(TaskFilterTab.OVERDUE) },
            modifier = Modifier.weight(1f),
            enabled = !overdueSelected,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (overdueSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Geçmiş Görevler",
                color = if (overdueSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onPrimary
            )
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
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (progress != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.width(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
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
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    task.endDateText?.let {
                        Text(
                            text = "Bitiş: $it",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
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
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    contentDescription = "Görev tamamlandı mı?",
                                    tint = if (task.isDone) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
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
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onAddSubTaskClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Alt görev ekle",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 *  Ekranın altındaki ayrı panel.
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
    onSubTaskClick: (Long) -> Unit,              //  sub task düzenleme
    onSubTaskDeleteRequest: (Long) -> Unit,      //  sub task silme isteği
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }

        Text(
            text = "Seçili görev: ${task.title}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        val subTasks = task.subTasks.orEmpty()

        if (subTasks.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bu görevin henüz alt görevi yok.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onAddSubTaskClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Alt görev ekle",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subTasks.forEach { subTask ->

                    SwipeableSubTaskCard(
                        subTask = subTask,
                        onToggleDone = { newValue ->
                            onSubTaskToggleDone(subTask.id, newValue)
                        },
                        onCountChange = { newCount ->
                            onSubTaskToggleDone(
                                subTask.id,
                                newCount >= (subTask.targetCount ?: 0).toInt()
                            )
                            onSubTaskCountChange(subTask.id, newCount)
                        },
                        onEdit = { id -> onSubTaskClick(id) },                     // SAĞA kaydır: düzenleme
                        onDeleteRequest = { id -> onSubTaskDeleteRequest(id) }     // SOLA kaydır: silme dialogu
                    )
                }
            }
        }
    }
}

/**
 *  Alt panelde görüntülenen sub task kartı.
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
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                modifier = Modifier.size(20.dp),
                                tint = if (subTask.isDone) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
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
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$current / $target",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val current = subTask.currentMinutes ?: 0
                            val target = subTask.targetMinutes ?: 0
                            Text(
                                text = "$current / $target dk",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// Main task kartını sağa/sola kaydırarak düzenleme / silme.
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableMainTaskItem(
    task: Main_task_ui_model,
    isExpanded: Boolean,
    onSubTaskToggleDone: (Long, Boolean) -> Unit,
    onSubTaskCountChange: (Long, Int) -> Unit,
    onCardClick: () -> Unit,
    onToggleDone: () -> Unit,
    onAddSubTaskClick: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { newValue ->
            when (newValue) {
                DismissValue.DismissedToEnd -> { // Start -> End (sağa kaydır)
                    onEdit(task.id)
                    false // kartı kapatma, eski yerine dönsün
                }
                DismissValue.DismissedToStart -> { // End -> Start (sola kaydır)
                    onDeleteRequest(task.id)
                    false
                }
                else -> true
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart
        ),
        background = {
            // Çok abartmadan, minimal bir arka plan gösterebiliriz.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol taraf (sağa kaydır = düzenle)
                Text(
                    text = "Düzenle",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                // Sağ taraf (sola kaydır = sil)
                Text(
                    text = "Sil",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissContent = {
            MainTaskItem(
                task = task,
                isExpanded = isExpanded,
                onSubTaskToggleDone = onSubTaskToggleDone,
                onSubTaskCountChange = onSubTaskCountChange,
                onCardClick = onCardClick,
                onToggleDone = onToggleDone,
                onAddSubTaskClick = onAddSubTaskClick
            )
        }
    )
}

//  Sub task kartını sağa/sola kaydırarak düzenleme / silme.
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableSubTaskCard(
    subTask: Sub_task_ui_model,
    onToggleDone: (Boolean) -> Unit,
    onCountChange: (Int) -> Unit,
    onEdit: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { newValue ->
            when (newValue) {
                DismissValue.DismissedToEnd -> { // sağa kaydır: düzenle
                    onEdit(subTask.id)
                    false
                }
                DismissValue.DismissedToStart -> { // sola kaydır: sil
                    onDeleteRequest(subTask.id)
                    false
                }
                else -> true
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart
        ),
        background = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Düzenle",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sil",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissContent = {
            SubTaskCard(
                subTask = subTask,
                onToggleDone = onToggleDone,
                onCountChange = onCountChange
            )
        }
    )
}


/**
 * MainScreen için light tema önizlemesi.
 */
@Preview(
    name = "MainScreen - Nature Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MainScreenLightPreview() {
    AppTheme(darkTheme = false) {
        MainScreen(
            state = Main_screen_state(), // Varsayılan, boş state. İstersen burada örnek task listesi doldurabilirsin.
            onRefresh = {},
            onMainTaskClick = {},
            onToggleMainTaskDone = {},
            onAddSubTaskClick = {},
            onAddMainTaskClick = {}
        )
    }
}

/**
 * MainScreen için dark tema önizlemesi.
 */
@Preview(
    name = "MainScreen - Nature Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainScreenDarkPreview() {
    AppTheme(darkTheme = true) {
        MainScreen(
            state = Main_screen_state(),
            onRefresh = {},
            onMainTaskClick = {},
            onToggleMainTaskDone = {},
            onAddSubTaskClick = {},
            onAddMainTaskClick = {}
        )
    }
}
@Preview(
    name = "MainScreen - Nature Light (Sample Data)",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MainScreenSampleLightPreview() {
    AppTheme(darkTheme = false) {
        MainScreen(
            state = sampleMainScreenState(),
            onRefresh = {},
            onMainTaskClick = {},
            onToggleMainTaskDone = {},
            onAddSubTaskClick = {},
            onAddMainTaskClick = {}
        )
    }
}

@Preview(
    name = "MainScreen - Nature Dark (Sample Data)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainScreenSampleDarkPreview() {
    AppTheme(darkTheme = true) {
        MainScreen(
            state = sampleMainScreenState(),
            onRefresh = {},
            onMainTaskClick = {},
            onToggleMainTaskDone = {},
            onAddSubTaskClick = {},
            onAddMainTaskClick = {}
        )
    }
}

// Preview’de kullanılacak sahte state
private fun sampleMainScreenState(): Main_screen_state {
    // BURASI ÖNEMLİ:
    // Main_task_ui_model ve Sub_task_ui_model'in constructor'ını
    // kendi projendeki tanıma göre named parametrelerle doldur.
    // Aşağıdaki örnek şemayı kendi data class'ına uyarlaman yeterli.

    val subTasksForFirst = listOf(
        Sub_task_ui_model(
            id = 101L,
            title = "Giriş kısmını oku",
            description = "Kitabın ilk 10 sayfası",
            taskType = Task_type_ui.Done,
            isDone = false,
            currentCount = 4,
            targetCount = 10,
            currentMinutes = null,
            targetMinutes = null,
            mainTaskId = 1,
            startDateText = TODO(),
            endDateText = TODO()
        ),
        Sub_task_ui_model(
            id = 102L,
            title = "Alıştırmaları çöz",
            description = "En az 5 soru",
            taskType = Task_type_ui.Count,   // Count ise senin yapına göre argüman eklemen gerekebilir
            isDone = false,
            currentCount = 2,
            targetCount = 5,
            currentMinutes = null,
            targetMinutes = null,
            mainTaskId = 1L,
            startDateText ="10.12.2023",
            endDateText ="10.12.2029"
        )
    )

    val upcoming = listOf(
        Main_task_ui_model(
            id = 1L,
            title = "İngilizce Çalış",
            description = "Her gün en az 30 dk okuma",
            hasSubTasks = true,
            totalSubTaskCount = subTasksForFirst.size,
            doneSubTaskCount = 1,
            taskType = Task_type_ui.Time,    // kendi sealed class yapına göre ayarla
            targetCount = null,
            currentCount = null,
            targetMinutes = 300,
            currentMinutes = 60,
            isDone = false,
            startDateText = "06.12.2025",
            endDateText = "31.12.2025",
            subTasks = subTasksForFirst
        ),
        Main_task_ui_model(
            id = 2L,
            title = "Spor",
            description = "Haftada 3 gün yürüyüş",
            hasSubTasks = false,
            totalSubTaskCount = 0,
            doneSubTaskCount = 0,
            taskType = Task_type_ui.Done,
            targetCount = null,
            currentCount = null,
            targetMinutes = null,
            currentMinutes = null,
            isDone = false,
            startDateText = "05.12.2025",
            endDateText = null,
            subTasks = emptyList()
        )
    )

    val overdue = listOf(
        Main_task_ui_model(
            id = 3L,
            title = "Veri Yapıları Not Çıkarma",
            description = "Stack, Queue, Linked List",
            hasSubTasks = false,
            totalSubTaskCount = 0,
            doneSubTaskCount = 0,
            taskType = Task_type_ui.Count,
            targetCount = 10,
            currentCount = 7,
            targetMinutes = null,
            currentMinutes = null,
            isDone = false,
            startDateText = "01.12.2025",
            endDateText = "03.12.2025",
            subTasks = emptyList()
        )
    )

    return Main_screen_state(
        isLoading = false,
        errorMessage = null,
        upcomingOrNoDeadlineMainTasks = upcoming,
        overdueMainTasks = overdue
    )
}
