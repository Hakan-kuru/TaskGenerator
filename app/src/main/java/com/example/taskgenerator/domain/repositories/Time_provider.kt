package com.example.taskgenerator.domain.repositories

interface Time_provider {
    fun nowMillis(): Long
}