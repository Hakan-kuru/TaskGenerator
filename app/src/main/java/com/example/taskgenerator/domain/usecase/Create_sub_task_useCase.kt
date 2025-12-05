package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.model.Sub_task
import com.example.taskgenerator.domain.repositories.Sub_repository
import javax.inject.Inject

class Create_sub_task_useCase @Inject constructor(
    private val repository: Sub_repository
) {
    suspend operator fun invoke(
        mainTaskId: Long,
        title: String,
        description: String?,
        taskType: String,      // "done" | "count" | "time"
        targetCount: Int?
    ) : Long {
        val subTask = Sub_task(
            id = 0L,                 // Room auto-generate edecek
            mainTaskId = mainTaskId,
            title = title,
            description = description,
            taskType = taskType,
            isDone = false,            // yeni alt görev başlangıçta tamamlanmamış
            currentCount = if (targetCount != null) 0 else null,
            targetCount = targetCount,
        )

        // Repository artık Long (yeni id) döndürüyor
        return repository.insertSubTask(subTask)
    }
}
