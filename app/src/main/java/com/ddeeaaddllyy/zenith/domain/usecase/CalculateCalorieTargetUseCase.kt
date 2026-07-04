package com.ddeeaaddllyy.zenith.domain.usecase

import com.ddeeaaddllyy.zenith.domain.model.ActivityLevel
import com.ddeeaaddllyy.zenith.domain.model.Gender
import com.ddeeaaddllyy.zenith.domain.model.Goal
import kotlin.math.roundToInt

/**
 * Mifflin-St Jeor BMR, scaled by activity level, then shifted for the user's goal.
 */
class CalculateCalorieTargetUseCase {

    operator fun invoke(
        gender: Gender,
        age: Int,
        heightCm: Int,
        weightKg: Double,
        activityLevel: ActivityLevel,
        goal: Goal
    ): Int {
        val bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + when (gender) {
            Gender.MALE -> 5.0
            Gender.FEMALE -> -161.0
        }
        val tdee = bmr * activityLevel.multiplier
        val target = when (goal) {
            Goal.LOSE -> tdee - 500
            Goal.MAINTAIN -> tdee
            Goal.GAIN -> tdee + 500
        }
        return target.roundToInt().coerceAtLeast(1200)
    }
}
