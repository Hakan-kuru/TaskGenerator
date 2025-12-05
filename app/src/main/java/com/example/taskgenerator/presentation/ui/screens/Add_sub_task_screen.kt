package com.example.taskgenerator.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.taskgenerator.presentation.uiModel.Task_type_ui
import com.example.taskgenerator.presentation.ui_states.Add_sub_task_ui_state

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_sub_task_screen(
    state: Add_sub_task_ui_state,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTaskTypeChange: (Task_type_ui) -> Unit,
    onTargetCountChange: (String) -> Unit,
    onTargetMinutesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Alt görev başarıyla kaydedildiğinde yukarıdan gelen onSaved tetikleniyor
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.parentTaskTitle
                            ?.let { "Alt Görev - $it" }
                            ?: "Yeni Alt Görev"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(if (state.isSaving) "Kaydediliyor..." else "Kaydet")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                },
                onClick = {
                    if (!state.isSaving) {
                        onSaveClick()
                    }
                },
                expanded = true
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Ana görev bilgisi (sadece bilgi amaçlı)
            Text(
                text = "Ana Görev ID: ${state.parentTaskId}",
                style = MaterialTheme.typography.labelSmall
            )
            state.parentTaskTitle?.let { title ->
                Text(
                    text = "Ana Görev: $title",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Başlık
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Alt Görev Başlığı") },
                isError = state.titleError != null,
                singleLine = true
            )
            if (state.titleError != null) {
                Text(
                    text = state.titleError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Açıklama
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                label = { Text("Açıklama (opsiyonel)") },
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Alt Görev Tipi", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = state.taskType is Task_type_ui.Done,
                        onClick = { onTaskTypeChange(Task_type_ui.Done) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tamamlandı / Tamamlanmadı")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = state.taskType is Task_type_ui.Count,
                        onClick = { onTaskTypeChange(Task_type_ui.Count) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sayıya bağlı alt görev (ör. 10 soru çöz)")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = state.taskType is Task_type_ui.Time,
                        onClick = { onTaskTypeChange(Task_type_ui.Time) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Zamana bağlı alt görev (dakika)")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // COUNT / TIME tipi için hedef alanı
            when (state.taskType) {
                is Task_type_ui.Count -> {
                    OutlinedTextField(
                        value = state.targetCountText,
                        onValueChange = onTargetCountChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hedef sayı") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        isError = state.targetError != null
                    )
                }

                is Task_type_ui.Time -> {
                    OutlinedTextField(
                        value = state.targetMinutesText,
                        onValueChange = onTargetMinutesChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hedef süre (dakika)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        isError = state.targetError != null
                    )
                }

                is Task_type_ui.Done -> {
                    // Done tipi için ekstra alan yok
                }
            }

            if (state.targetError != null) {
                Text(
                    text = state.targetError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Genel hata mesajı
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
