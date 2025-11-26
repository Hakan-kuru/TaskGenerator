package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.repositories.Sub_repository
import javax.inject.Inject

class Delete_sub_task_useCase @Inject constructor(
    private val repository: Sub_repository
) {
    suspend operator fun invoke(subTaskId: Long) {
        repository.deleteSubTask(subTaskId)
    }
}