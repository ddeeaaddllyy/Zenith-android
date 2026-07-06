package com.ddeeaaddllyy.zenith.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps_baseline")
data class StepsBaselineEntity(
    @PrimaryKey val id: Int = 1,
    val date: String,
    val baselineCount: Int
)
