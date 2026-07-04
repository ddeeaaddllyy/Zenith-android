package com.ddeeaaddllyy.zenith.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ddeeaaddllyy.zenith.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY date ASC, id ASC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Insert
    suspend fun insert(entry: WeightEntryEntity): Long
}
