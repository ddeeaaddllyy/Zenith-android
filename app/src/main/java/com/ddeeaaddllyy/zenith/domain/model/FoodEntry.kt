package com.ddeeaaddllyy.zenith.domain.model

import java.time.LocalDate

data class FoodEntry(
    val id: Long = 0,
    val date: LocalDate,
    val name: String,
    val calories: Int,
    val proteinG: Double? = null,
    val fatG: Double? = null,
    val carbsG: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)
