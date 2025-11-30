package com.example.taskgenerator.domain.model

data class Deadline_grouped_main_tasks(
    val overdue: List<Main_with_sub_tasks>,   // deadline < now
    val upcomingOrNoDeadline: List<Main_with_sub_tasks> // deadline >= now veya null
)