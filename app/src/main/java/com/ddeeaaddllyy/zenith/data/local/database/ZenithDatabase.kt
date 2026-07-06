package com.ddeeaaddllyy.zenith.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ddeeaaddllyy.zenith.data.local.dao.FoodEntryDao
import com.ddeeaaddllyy.zenith.data.local.dao.NedovolenAccountDao
import com.ddeeaaddllyy.zenith.data.local.dao.StepsBaselineDao
import com.ddeeaaddllyy.zenith.data.local.dao.UserProfileDao
import com.ddeeaaddllyy.zenith.data.local.dao.WeightEntryDao
import com.ddeeaaddllyy.zenith.data.local.dao.WorkoutEntryDao
import com.ddeeaaddllyy.zenith.data.local.entity.FoodEntryEntity
import com.ddeeaaddllyy.zenith.data.local.entity.NedovolenAccountEntity
import com.ddeeaaddllyy.zenith.data.local.entity.StepsBaselineEntity
import com.ddeeaaddllyy.zenith.data.local.entity.UserProfileEntity
import com.ddeeaaddllyy.zenith.data.local.entity.WeightEntryEntity
import com.ddeeaaddllyy.zenith.data.local.entity.WorkoutEntryEntity

@Database(
    entities = [
        UserProfileEntity::class,
        FoodEntryEntity::class,
        WorkoutEntryEntity::class,
        WeightEntryEntity::class,
        NedovolenAccountEntity::class,
        StepsBaselineEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class ZenithDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun workoutEntryDao(): WorkoutEntryDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun nedovolenAccountDao(): NedovolenAccountDao
    abstract fun stepsBaselineDao(): StepsBaselineDao
}
