package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.repositories.Main_repository
import javax.inject.Inject

class Delete_main_task_useCase @Inject constructor(
    private val taskRepository: Main_repository
) {
    suspend operator fun invoke(mainTaskId: Long) {
        taskRepository.deleteMainTaskWithSubTasks(mainTaskId)
    }
}