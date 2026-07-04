package com.ddeeaaddllyy.zenith.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ddeeaaddllyy.zenith.data.local.entity.FoodEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY createdAt DESC")
    fun entriesForDate(date: String): Flow<List<FoodEntryEntity>>

    @Query("SELECT * FROM food_entries WHERE date BETWEEN :start AND :end ORDER BY date ASC, createdAt ASC")
    fun entriesBetween(start: String, end: String): Flow<List<FoodEntryEntity>>

    @Insert
    suspend fun insert(entry: FoodEntryEntity): Long

    @Delete
    suspend fun delete(entry: FoodEntryEntity)
}
