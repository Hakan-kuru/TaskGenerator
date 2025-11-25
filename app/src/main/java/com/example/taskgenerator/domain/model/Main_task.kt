package com.example.taskgenerator.domain.model

// Domain katmanında kullanılacak ana görev modeli.
data class Main_task(
    val id: Long,
    val title: String,
    val description: String? = null,
    val taskType: String,        // "done", "count", "time" gibi 3 tipten biri (db'de string tutuyorduk)
    val targetCount: Int? = null, // Hedef değer (sayı ya da dakika). Sadece ilgili tip için anlamlı.
    val currentCount: Int? = 0,    // Mevcut ilerleme (sayı ya da dakika).
    val isDone: Boolean = false   // Görev tamamlandı mı?
)
