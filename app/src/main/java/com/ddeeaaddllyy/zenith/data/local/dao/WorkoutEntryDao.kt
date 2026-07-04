package com.ddeeaaddllyy.zenith.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ddeeaaddllyy.zenith.data.local.entity.WorkoutEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutEntryDao {
    @Query("SELECT * FROM workout_entries WHERE date = :date ORDER BY createdAt DESC")
    fun entriesForDate(date: String): Flow<List<WorkoutEntryEntity>>

    @Query("SELECT * FROM workout_entries WHERE date BETWEEN :start AND :end ORDER BY date ASC, createdAt ASC")
    fun entriesBetween(start: String, end: String): Flow<List<WorkoutEntryEntity>>

    @Insert
    suspend fun insert(entry: WorkoutEntryEntity): Long

    @Delete
    suspend fun delete(entry: WorkoutEntryEntity)
}
