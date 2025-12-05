package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.repositories.Sub_repository
import javax.inject.Inject

class Update_sub_task_progress_useCase @Inject constructor(
    private val repository: Sub_repository
) {
    suspend operator fun invoke(
        subTaskId: Long,
        isDone: Boolean? = false,
        currentCount: Int? = null
    ) {
        repository.updateSubTaskProgress(
            subTaskId = subTaskId,
            isDone = isDone,
            currentCount = currentCount
        )
    }
}