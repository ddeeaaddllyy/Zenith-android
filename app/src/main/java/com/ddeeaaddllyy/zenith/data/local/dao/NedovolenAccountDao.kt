package com.ddeeaaddllyy.zenith.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ddeeaaddllyy.zenith.data.local.entity.NedovolenAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NedovolenAccountDao {
    @Query("SELECT * FROM nedovolen_account WHERE id = 1")
    fun observeSession(): Flow<NedovolenAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NedovolenAccountEntity)

    @Query("DELETE FROM nedovolen_account WHERE id = 1")
    suspend fun clear()
}
