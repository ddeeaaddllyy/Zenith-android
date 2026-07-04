package com.ddeeaaddllyy.zenith.data.repository

import com.ddeeaaddllyy.zenith.data.local.dao.WorkoutEntryDao
import com.ddeeaaddllyy.zenith.data.local.entity.WorkoutEntryEntity
import com.ddeeaaddllyy.zenith.domain.model.WorkoutEntry
import com.ddeeaaddllyy.zenith.domain.model.WorkoutType
import com.ddeeaaddllyy.zenith.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WorkoutRepositoryImpl(private val dao: WorkoutEntryDao) : WorkoutRepository {

    override fun entriesForDate(date: LocalDate): Flow<List<WorkoutEntry>> =
        dao.entriesForDate(date.toString()).map { list -> list.map { it.toDomain() } }

    override fun entriesBetween(start: LocalDate, end: LocalDate): Flow<List<WorkoutEntry>> =
        dao.entriesBetween(start.toString(), end.toString()).map { list -> list.map { it.toDomain() } }

    override suspend fun addEntry(entry: WorkoutEntry) {
        dao.insert(entry.toEntity())
    }

    override suspend fun deleteEntry(entry: WorkoutEntry) {
        dao.delete(entry.toEntity())
    }

    private fun WorkoutEntryEntity.toDomain() = WorkoutEntry(
        id = id,
        date = LocalDate.parse(date),
        type = WorkoutType.valueOf(type),
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        note = note,
        createdAt = createdAt
    )

    private fun WorkoutEntry.toEntity() = WorkoutEntryEntity(
        id = id,
        date = date.toString(),
        type = type.name,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        note = note,
        createdAt = createdAt
    )
}
