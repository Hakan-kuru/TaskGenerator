package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.domain.repositories.Main_repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Get_main_task_with_sub_tasks_useCase @Inject constructor(
    private val repository: Main_repository
) {
    operator fun invoke(mainTaskId: Long): Flow<Main_with_sub_tasks?> {
        return repository.getMainTaskWithSubTasks(mainTaskId)
    }
}