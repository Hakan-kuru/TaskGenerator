package com.example.taskgenerator.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.taskgenerator.presentation.uiModel.Task_type_ui
import com.example.taskgenerator.presentation.ui_states.Create_main_task_state
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Create_main_task_screen(
    state: Create_main_task_state,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTaskTypeChange: (Task_type_ui) -> Unit,
    onTargetCountChange: (String) -> Unit,
    onDeadlineSelected: (Long) -> Unit,
    onSaveClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Kayıt başarılı olduğunda yukarıdan gelen onSaved tetikleniyor
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    // Bugünün başını (00:00) millis cinsinden hesaplayalım
    val todayStartMillis = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.deadlineDateMillis ?: todayStartMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayStartMillis
            }
        }
    )

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            onDeadlineSelected(selected)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Tamam",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "İptal",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Yeni Görev Oluştur",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = if (state.isSaving) "Kaydediliyor..." else "Kaydet",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                onClick = {
                    if (!state.isSaving) {
                        onSaveClick()
                    }
                },
                expanded = true,
                containerColor = MaterialTheme.colorScheme.primary
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
            // Başlık
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Başlık") },
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
                    .heightIn(min = 100.dp),
                label = { Text("Açıklama (opsiyonel)") },
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Görev Tipi", 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = state.taskType == Task_type_ui.Done,
                        onClick = { onTaskTypeChange(Task_type_ui.Done) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tamamlandı / Tamamlanmadı",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = state.taskType == Task_type_ui.Count,
                        onClick = { onTaskTypeChange(Task_type_ui.Count) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sayıya bağlı görev (ör. 20 soru çöz)",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = state.taskType == Task_type_ui.Time,
                        onClick = { onTaskTypeChange(Task_type_ui.Time) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Zamana bağlı görev (dakika)",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hedef Bitiş Tarihi", 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val deadlineText = state.deadlineDateMillis?.let { millis ->
                    formatDateForUi(millis)
                } ?: "Seçilmedi"

                Text(
                    text = deadlineText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Tarih Seç",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // COUNT / TIME tipi için hedef alanı
            if (state.taskType == Task_type_ui.Count || state.taskType == Task_type_ui.Time) {
                val label = if (state.taskType == Task_type_ui.Count) {
                    "Hedef sayı"
                } else {
                    "Hedef süre (dakika)"
                }

                OutlinedTextField(
                    value = state.targetCountText,
                    onValueChange = onTargetCountChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hata mesajı
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

private fun formatDateForUi(millis: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(millis)
}
