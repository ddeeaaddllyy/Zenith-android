package com.ddeeaaddllyy.zenith.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ddeeaaddllyy.zenith.data.local.entity.StepsBaselineEntity

@Dao
interface StepsBaselineDao {
    @Query("SELECT * FROM steps_baseline WHERE id = 1")
    suspend fun get(): StepsBaselineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StepsBaselineEntity)
}
