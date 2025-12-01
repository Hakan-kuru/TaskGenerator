package com.example.taskgenerator.presentation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // İLK KEZ kullanıyoruz: state değişince yan etki (navigasyon) çalıştırmak için.
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel // İLK KEZ kullanıyoruz: Hilt ile ViewModel alma helper'ı.
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.taskgenerator.presentation.view_model.Create_main_task_vm
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Create_main_task_screen(
    navController: NavController,
    viewModel: Create_main_task_vm = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
        // Sadece bugünden sonraki günlerin seçilebilmesi için selectableDates
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayStartMillis
            }
        }
    )

    // Kayıt başarılı olduğunda geri dön
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            // main ekrana geri dönüyoruz
            navController.popBackStack()
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            viewModel.onDeadlineSelected(selected)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Tamam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("İptal")
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
                title = { Text(text = "Yeni Görev Oluştur") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                        imageVector = Icons.Default.ArrowBack, // burada proper bir icon seç
                        contentDescription = null
                    )
                },
                onClick = {
                    if (!state.isSaving) {
                        viewModel.onSaveClicked()
                    }
                },
                expanded = true
            )
        }

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
                onValueChange = { viewModel.onTitleChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Başlık") },
                isError = state.titleError != null,
                singleLine = true
            )
            if (state.titleError != null) {
                Text(
                    text = state.titleError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Açıklama
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                label = { Text("Açıklama (opsiyonel)") },
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Görev Tipi", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Görev tipi seçimleri (DONE / COUNT / TIME)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        RadioButton(
                            selected = state.taskType == "DONE",
                            onClick = { viewModel.onTaskTypeChange("DONE") }
                        )
                        Text("Tamamlandı / Tamamlanmadı")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        RadioButton(
                            selected = state.taskType == "COUNT",
                            onClick = { viewModel.onTaskTypeChange("COUNT") }
                        )
                        Text("Sayıya bağlı görev (ör. 20 soru çöz)")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        RadioButton(
                            selected = state.taskType == "TIME",
                            onClick = { viewModel.onTaskTypeChange("TIME") }
                        )
                        Text("Zamana bağlı görev (dakika)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Hedef Bitiş Tarihi", style = MaterialTheme.typography.titleMedium)
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
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(onClick = { showDatePicker = true }) {
                    Text("Tarih Seç")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hata mesajı
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // COUNT / TIME tipi için hedef alanı
            if (state.taskType == "COUNT" || state.taskType == "TIME") {
                val label = if (state.taskType == "COUNT") {
                    "Hedef sayı"
                } else {
                    "Hedef süre (dakika)"
                }

                OutlinedTextField(
                    value = state.targetCountText,
                    onValueChange = { viewModel.onTargetCountChange(it) },
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
                Spacer(modifier = Modifier.height(8.dp))
            }

            // İleride tarih seçici, alt görev ekleme vs. buraya gelebilir.
        }
    }
}

private fun formatDateForUi(millis: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(millis)
}