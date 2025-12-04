package com.example.taskgenerator.utils

import android.R.attr.end
import androidx.room.coroutines.createFlow
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.model.Sub_task
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.presentation.uiModel.Main_task_ui_model
import com.example.taskgenerator.presentation.uiModel.Sub_task_ui_model
import com.example.taskgenerator.presentation.uiModel.Task_type_ui
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ----------------------
// taskType String -> Task_type_ui
// ----------------------

// Bu fonksiyonu ilk defa kullanıyoruz: domain string tip --> UI sealed class dönüşümü.
fun String.toTaskTypeUi(): Task_type_ui {
    return when (this.lowercase()) {
        "count" -> Task_type_ui.Count
        "time" -> Task_type_ui.Time
        "done" -> Task_type_ui.Done
        else -> Task_type_ui.Done   // bilinmeyen durumda defaults
    }
}

// Gerekirse tersine dönüşüm de:
// Task_type_ui -> String (usecase / repository'ye geri yazarken)
fun Task_type_ui.toDomainString(): String = this.rawValue

// ----------------------
// Tarih Long -> String
// ----------------------

private val dateFormatter by lazy {
    // Tarihleri "12.11.2025" gibi göstermek için
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
}

fun dateStringToLong(dateString: String): Long? {
    return try {
        dateFormatter.parse(dateString)?.time   // <-- milisaniye olarak Long
    } catch (e: Exception) {
        null
    }
}
// Long? -> "dd.MM.yyyy" veya null
fun Long?.toDateTextOrNull(): String? {
    return this?.let { millis ->
        dateFormatter.format(Date(millis))
    }
}

// ----------------------
// Main_with_sub_tasks -> Main_task_ui_model
// ----------------------

// Burada Main_with_sub_tasks içinden:
// - hasSubTasks
// - doneSubTaskCount
// - totalSubTaskCount
// gibi ek alanları hesaplıyoruz.
fun Main_with_sub_tasks.toUiModel(): Main_task_ui_model {
    val main: Main_task = this.mainTask
    val subs: List<Sub_task> = this.subTasks

    val hasSubTasks = subs.isNotEmpty()
    val totalSubTaskCount = subs.size
    val doneSubTaskCount = subs.count { it.isDone }

    val taskTypeUi = main.taskType.toTaskTypeUi()

    // Şu an domain'de createdAt/updatedAt var; bunları UI'da başlangıç/bitiş gibi kullanabiliriz. :contentReference[oaicite:9]{index=9}
    val startDateText = main.createdAt.toDateTextOrNull()
    val endDateText = main.updatedAt.toDateTextOrNull()

    return Main_task_ui_model(
        id = main.id ?:0L,
        title = main.title,
        description = main.description.orEmpty(),
        taskType = taskTypeUi,

        subTasks = subs.map { it.toUiModel() },
        hasSubTasks = hasSubTasks,
        doneSubTaskCount = doneSubTaskCount,
        totalSubTaskCount = totalSubTaskCount,

        isDone = main.isDone,
        currentCount = main.currentCount,
        targetCount = main.targetCount,

        // Time tipinde görevler için: currentCount/targetCount dakikayı temsil ediyor.
        // İstersen bunları UI'da kullanmak yerine direkt currentCount/targetCount üzerinden gidebilirsin.
        currentMinutes = if (taskTypeUi is Task_type_ui.Time) main.currentCount else null,
        targetMinutes = if (taskTypeUi is Task_type_ui.Time) main.targetCount else null,

        startDateText = startDateText,
        endDateText = endDateText
    )
}

fun Sub_task.toUiModel(): Sub_task_ui_model{

    val taskTypeUi = taskType.toTaskTypeUi()
    return Sub_task_ui_model(
        id = id,
        mainTaskId = mainTaskId,
        title = title,
        description = description.orEmpty(),
        taskType = taskTypeUi,
        isDone = isDone,
        currentCount = currentCount,
        targetCount = targetCount,
        currentMinutes = if (taskTypeUi is Task_type_ui.Time) currentCount else null,
        targetMinutes = if (taskTypeUi is Task_type_ui.Time) targetCount else null,
        startDateText = createdAt.toDateTextOrNull(),
        endDateText = updatedAt.toDateTextOrNull(),
    )
}
fun Sub_task_ui_model.toDomain(): Sub_task{
    val start =dateStringToLong(startDateText!!)
    val update =dateStringToLong(endDateText!!)
    return Sub_task(
        id = 0L,
        mainTaskId = mainTaskId,
        title = title,
        description = description,
        taskType = taskType.toDomainString(),
        targetCount = targetCount,
        currentCount = currentCount,
        isDone = isDone,
        createdAt = start!!,
        updatedAt = update!!
    )
}

