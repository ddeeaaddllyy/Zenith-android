package com.ddeeaaddllyy.zenith.domain.repository

import com.ddeeaaddllyy.zenith.domain.model.WorkoutEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutRepository {
    fun entriesForDate(date: LocalDate): Flow<List<WorkoutEntry>>
    fun entriesBetween(start: LocalDate, end: LocalDate): Flow<List<WorkoutEntry>>
    suspend fun addEntry(entry: WorkoutEntry)
    suspend fun deleteEntry(entry: WorkoutEntry)
}
