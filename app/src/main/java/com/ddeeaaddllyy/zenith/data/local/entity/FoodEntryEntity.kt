package com.ddeeaaddllyy.zenith.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val name: String,
    val calories: Int,
    val proteinG: Double?,
    val fatG: Double?,
    val carbsG: Double?,
    val createdAt: Long
)
