package com.ddeeaaddllyy.zenith.domain.model

enum class Gender(val label: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

enum class ActivityLevel(val label: String, val shortLabel: String, val multiplier: Double) {
    SEDENTARY("Минимальная (сидячий образ жизни)", "Минимальная", 1.2),
    LIGHT("Лёгкая (1-3 тренировки в неделю)", "Лёгкая", 1.375),
    MODERATE("Средняя (3-5 тренировок в неделю)", "Средняя", 1.55),
    ACTIVE("Высокая (6-7 тренировок в неделю)", "Высокая", 1.725),
    VERY_ACTIVE("Очень высокая (спорт каждый день)", "Очень высокая", 1.9)
}

enum class Goal(val label: String) {
    LOSE("Похудение"),
    MAINTAIN("Поддержание веса"),
    GAIN("Набор массы")
}

data class UserProfile(
    val name: String,
    val gender: Gender,
    val age: Int,
    val heightCm: Int,
    val startWeightKg: Double,
    val currentWeightKg: Double,
    val targetWeightKg: Double,
    val activityLevel: ActivityLevel,
    val goal: Goal,
    val dailyCalorieTarget: Int
) {
    val weightChangeKg: Double
        get() = currentWeightKg - startWeightKg
}
