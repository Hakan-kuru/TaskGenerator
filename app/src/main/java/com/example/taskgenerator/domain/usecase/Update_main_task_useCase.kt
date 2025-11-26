package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.repositories.Main_repository
import javax.inject.Inject

class Update_main_task_useCase @Inject constructor(
    private val repository: Main_repository
) {
    suspend operator fun invoke(mainTask: Main_task) {
        repository.updateMainTask(mainTask)
    }
}