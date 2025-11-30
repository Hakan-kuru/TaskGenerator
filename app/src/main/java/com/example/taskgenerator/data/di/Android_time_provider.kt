package com.example.taskgenerator.data.di

import com.example.taskgenerator.domain.repositories.Time_provider
import javax.inject.Inject

class Android_time_provider @Inject constructor() : Time_provider {
    override fun nowMillis(): Long = System.currentTimeMillis()
}