package com.example.taskgenerator.domain.usecase

import com.example.taskgenerator.domain.model.Deadline_grouped_main_tasks
import com.example.taskgenerator.domain.model.Main_with_sub_tasks
import com.example.taskgenerator.domain.repositories.Main_repository
import com.example.taskgenerator.domain.repositories.Time_provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Get_main_task_with_sub_tasks_useCase @Inject constructor(
    private val repository: Main_repository,
    private val timeProvider: Time_provider
) {


    operator fun invoke(): Flow<Deadline_grouped_main_tasks> {
        return repository.getAllMainTasksWithSubTasks()
            .map { list ->
                val now = timeProvider.nowMillis()

                val (overdue, upcomingOrNoDeadline) = list.partition { mainWithSub ->
                    val deadline = mainWithSub.mainTask.updatedAt // kendi Main_task modelindeki field ismine göre düzelt
                    deadline < now
                }

                Deadline_grouped_main_tasks(
                    overdue = overdue,
                    upcomingOrNoDeadline = upcomingOrNoDeadline
                )
            }
    }
}