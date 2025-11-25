package com.example.taskgenerator.utils


import com.example.taskgenerator.data.entity.Main_task_entity
import com.example.taskgenerator.data.entity.Main_task_with_sub_tasks_entity
import com.example.taskgenerator.data.entity.Sub_task_entity
import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.domain.model.Sub_task

// Buradaki extension fonksiyonlar, Entity -> Domain dönüşümü için kullanılıyor.
// Böylece data katmanı Room'a, domain katmanı ise sade modellere bağımlı kalıyor.

// MainTaskEntity -> MainTask (Entity'den domain'e çevirim)
fun Main_task_entity.toDomain(): Main_task {
    return Main_task(
        id = mainTaskId,
        title = title,
        description = description,
        taskType = taskType.toString(),
        targetCount = targetCount,
        currentCount = currentCount,
        isDone = isDone
    )
}

// MainTask -> MainTaskEntity (Domain'den entity'e çevirim)
fun Main_task.toEntity(): Main_task_entity {
    return Main_task_entity(
        mainTaskId = id,
        title = title,
        description = description.toString(),
        taskType = taskType,
        targetCount = targetCount,
        currentCount = currentCount,
        isDone = isDone
    )
}

// SubTaskEntity -> SubTask
fun Sub_task_entity.toDomain(): Sub_task {
    return Sub_task(
        id = id,
        mainTaskId = mainTaskId,
        title = title,
        description = description,
        taskType = taskType.toString(),
        targetCount = targetCount,
        currentCount = currentCount,
        isDone = isDone
    )
}

// SubTask -> SubTaskEntity
fun Sub_task.toEntity(): Sub_task_entity {
    return Sub_task_entity(
        id = id,
        mainTaskId = mainTaskId,
        title = title,
        description = description.toString(),
        taskType = taskType,
        targetCount = targetCount,
        currentCount = currentCount,
        isDone = isDone
    )
}

// Room'daki relation sınıfını domain'deki relation'a dönüştürüyoruz.
fun Main_task_with_sub_tasks_entity.toDomain(): Main_with_sub_tasks {
    return Main_with_sub_tasks(
        mainTask = mainTask.toDomain(),
        subTasks = subTasks.map { it.toDomain() }
    )
}
