package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.model.Main_task
import com.example.taskgenerator.domain.repositories.Main_repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Get_main_tasks_useCase @Inject constructor(
    private val repository: Main_repository
) {
    // operator fun invoke: Bu sayede use case'i
    // getMainTasksUseCase() şeklinde fonksiyon gibi çağırabiliyoruz.
    operator fun invoke(): Flow<List<Main_task>> {
        return repository.getAllMainTasks()
    }
}
