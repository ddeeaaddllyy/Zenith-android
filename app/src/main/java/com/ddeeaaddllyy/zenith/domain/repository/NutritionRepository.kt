package com.ddeeaaddllyy.zenith.domain.repository

import com.ddeeaaddllyy.zenith.domain.model.FoodEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface NutritionRepository {
    fun entriesForDate(date: LocalDate): Flow<List<FoodEntry>>
    fun entriesBetween(start: LocalDate, end: LocalDate): Flow<List<FoodEntry>>
    suspend fun addEntry(entry: FoodEntry)
    suspend fun deleteEntry(entry: FoodEntry)
}
