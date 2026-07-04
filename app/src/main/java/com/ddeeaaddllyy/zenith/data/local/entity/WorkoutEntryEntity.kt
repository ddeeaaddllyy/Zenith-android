package com.ddeeaaddllyy.zenith.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_entries")
data class WorkoutEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val note: String?,
    val createdAt: Long
)
