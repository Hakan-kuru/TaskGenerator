package com.example.taskgenerator.presentation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class Add_sub_task_form(
    val parentTaskId: Long,
    val title: String,
    val description: String,
    val taskType: Task_type_ui,
    val targetCount: Int?,
    val targetMinutes: Int?
)

/**
 * SubTask ekleme ekranı.
 *
 * parentTaskTitle isteğe bağlı; ekranda hangi mainTask için alt görev açtığını gösterir.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Sub_task_screen(
    parentTaskId: Long,
    parentTaskTitle: String?,
    onBackClick: () -> Unit,
    onSaveClick: (Add_sub_task_form) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<Task_type_ui>(Task_type_ui.Done) }
    var targetCountText by remember { mutableStateOf("") }
    var targetMinutesText by remember { mutableStateOf("") }

    val isDoneType = selectedType is Task_type_ui.Done
    val isCountType = selectedType is Task_type_ui.Count
    val isTimeType = selectedType is Task_type_ui.Time

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = parentTaskTitle?.let { "Alt Görev - $it" } ?: "Yeni Alt Görev"
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
                text = "Alt görev tipi",
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val targetCount = targetCountText.toIntOrNull()
                    val targetMinutes = targetMinutesText.toIntOrNull()

                    val form = Add_sub_task_form(
                        parentTaskId = parentTaskId,
                        title = title.trim(),
                        description = description.trim(),
                        taskType = selectedType,
                        targetCount = if (isCountType) targetCount else null,
                        targetMinutes = if (isTimeType) targetMinutes else null
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
