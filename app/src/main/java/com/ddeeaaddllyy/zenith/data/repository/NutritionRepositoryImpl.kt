package com.ddeeaaddllyy.zenith.data.repository

import com.ddeeaaddllyy.zenith.data.local.dao.FoodEntryDao
import com.ddeeaaddllyy.zenith.data.local.entity.FoodEntryEntity
import com.ddeeaaddllyy.zenith.domain.model.FoodEntry
import com.ddeeaaddllyy.zenith.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class NutritionRepositoryImpl(private val dao: FoodEntryDao) : NutritionRepository {

    override fun entriesForDate(date: LocalDate): Flow<List<FoodEntry>> =
        dao.entriesForDate(date.toString()).map { list -> list.map { it.toDomain() } }

    override fun entriesBetween(start: LocalDate, end: LocalDate): Flow<List<FoodEntry>> =
        dao.entriesBetween(start.toString(), end.toString()).map { list -> list.map { it.toDomain() } }

    override suspend fun addEntry(entry: FoodEntry) {
        dao.insert(entry.toEntity())
    }

    override suspend fun deleteEntry(entry: FoodEntry) {
        dao.delete(entry.toEntity())
    }

    private fun FoodEntryEntity.toDomain() = FoodEntry(
        id = id,
        date = LocalDate.parse(date),
        name = name,
        calories = calories,
        proteinG = proteinG,
        fatG = fatG,
        carbsG = carbsG,
        createdAt = createdAt
    )

    private fun FoodEntry.toEntity() = FoodEntryEntity(
        id = id,
        date = date.toString(),
        name = name,
        calories = calories,
        proteinG = proteinG,
        fatG = fatG,
        carbsG = carbsG,
        createdAt = createdAt
    )
}
