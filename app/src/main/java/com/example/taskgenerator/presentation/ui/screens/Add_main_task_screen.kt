package com.example.taskgenerator.presentation.ui.screens

import androidx.compose.material.icons.Icons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType


// Formu ViewModel'e taşımak istersen bu data class'ı kullanabilirsin.
data class Add_main_task_form(
    val title: String,
    val description: String,
    val taskType: Task_type_ui,
    val targetCount: Int?,
    val targetMinutes: Int?,
    val startDate: String?,
    val endDate: String?
)

/**
 * MainTask ekleme ekranı.
 *
 * Şu an local state kullanıyor. onSave callback'i ile ViewModel/useCase tarafına formu gönderebilirsin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_main_task_form_screen(
    onBackClick: () -> Unit,
    onSaveClick: (Add_main_task_form) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<Task_type_ui>(Task_type_ui.Done) }
    var targetCountText by remember { mutableStateOf("") }
    var targetMinutesText by remember { mutableStateOf("") }
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }

    val isDoneType = selectedType is Task_type_ui.Done
    val isCountType = selectedType is Task_type_ui.Count
    val isTimeType = selectedType is Task_type_ui.Time

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Main Task") },
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
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Başlık") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Açıklama") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
            )

            Text(
                text = "Görev tipi",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isDoneType,
                    onClick = { selectedType = Task_type_ui.Done },
                    label = { Text("Done") }
                )
                FilterChip(
                    selected = isCountType,
                    onClick = { selectedType = Task_type_ui.Count },
                    label = { Text("Count") }
                )
                FilterChip(
                    selected = isTimeType,
                    onClick = { selectedType = Task_type_ui.Time },
                    label = { Text("Time") }
                )
            }

            if (isCountType) {
                OutlinedTextField(
                    value = targetCountText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() }) {
                            targetCountText = new
                        }
                    },
                    label = { Text("Hedef sayı") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (isTimeType) {
                OutlinedTextField(
                    value = targetMinutesText,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() }) {
                            targetMinutesText = new
                        }
                    },
                    label = { Text("Hedef süre (dk)") },
                    singleLine = true,
                    keyboardOptions =KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Tarih alanlarını şimdilik plain text bıraktım.
            // İleride date picker ile değiştirebilirsin.
            OutlinedTextField(
                value = startDateText,
                onValueChange = { startDateText = it },
                label = { Text("Başlangıç tarihi (opsiyonel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endDateText,
                onValueChange = { endDateText = it },
                label = { Text("Bitiş tarihi (opsiyonel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val targetCount = targetCountText.toIntOrNull()
                    val targetMinutes = targetMinutesText.toIntOrNull()

                    val form = Add_main_task_form(
                        title = title.trim(),
                        description = description.trim(),
                        taskType = selectedType,
                        targetCount = if (isCountType) targetCount else null,
                        targetMinutes = if (isTimeType) targetMinutes else null,
                        startDate = startDateText.ifBlank { null },
                        endDate = endDateText.ifBlank { null }
                    )
                    onSaveClick(form)
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Kaydet")
            }
        }
    }
}

