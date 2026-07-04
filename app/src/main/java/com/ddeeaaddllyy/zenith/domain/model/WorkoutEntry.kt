package com.ddeeaaddllyy.zenith.domain.model

import java.time.LocalDate

enum class WorkoutType(val label: String) {
    STRENGTH("Силовая тренировка"),
    CARDIO("Кардио"),
    WALKING("Ходьба"),
    CYCLING("Велоспорт"),
    SWIMMING("Плавание"),
    YOGA("Йога / растяжка"),
    OTHER("Другое")
}

data class WorkoutEntry(
    val id: Long = 0,
    val date: LocalDate,
    val type: WorkoutType,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
