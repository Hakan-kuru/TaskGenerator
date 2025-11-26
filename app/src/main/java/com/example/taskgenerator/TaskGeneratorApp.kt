package com.example.taskgenerator

import android.app.Application
// Hilt Application anotasyonu (ilk defa kullanıyoruz)
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskGeneratorApp : Application()