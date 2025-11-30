package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.repositories.Main_repository
import javax.inject.Inject

class Toggle_main_task_done_usecase @Inject constructor(
    private val repository: Main_repository
) {
    suspend operator fun invoke(mainTaskId: Long) {
        // mainTask'ın mevcut isDone değerini repository içinde
        // tersine çeviren metodu çağırıyoruz.
        repository.toggleMainTaskDone(mainTaskId)
    }
}