package com.ddeeaaddllyy.zenith.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val gender: String,
    val age: Int,
    val heightCm: Int,
    val startWeightKg: Double,
    val currentWeightKg: Double,
    val targetWeightKg: Double,
    val activityLevel: String,
    val goal: String,
    val dailyCalorieTarget: Int,
    val theme: String = "CHOCOLATTE"
)
