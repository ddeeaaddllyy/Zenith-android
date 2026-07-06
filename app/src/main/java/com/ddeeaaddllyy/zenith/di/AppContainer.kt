package com.ddeeaaddllyy.zenith.di

import android.content.Context
import androidx.room.Room
import com.ddeeaaddllyy.zenith.data.local.database.ZenithDatabase
import com.ddeeaaddllyy.zenith.data.repository.NedovolenAccountRepositoryImpl
import com.ddeeaaddllyy.zenith.data.repository.NutritionRepositoryImpl
import com.ddeeaaddllyy.zenith.data.repository.StepsRepositoryImpl
import com.ddeeaaddllyy.zenith.data.repository.UserRepositoryImpl
import com.ddeeaaddllyy.zenith.data.repository.WeightRepositoryImpl
import com.ddeeaaddllyy.zenith.data.repository.WorkoutRepositoryImpl
import com.ddeeaaddllyy.zenith.domain.repository.NedovolenAccountRepository
import com.ddeeaaddllyy.zenith.domain.repository.NutritionRepository
import com.ddeeaaddllyy.zenith.domain.repository.StepsRepository
import com.ddeeaaddllyy.zenith.domain.repository.UserRepository
import com.ddeeaaddllyy.zenith.domain.repository.WeightRepository
import com.ddeeaaddllyy.zenith.domain.repository.WorkoutRepository
import com.ddeeaaddllyy.zenith.domain.usecase.CalculateCalorieTargetUseCase

class AppContainer(context: Context) {

    private val database: ZenithDatabase = Room.databaseBuilder(
        context.applicationContext,
        ZenithDatabase::class.java,
        "zenith.db"
    )
        // This app has no released users yet — destructive fallback keeps iteration fast.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    val userRepository: UserRepository = UserRepositoryImpl(database.userProfileDao())
    val nutritionRepository: NutritionRepository = NutritionRepositoryImpl(database.foodEntryDao())
    val workoutRepository: WorkoutRepository = WorkoutRepositoryImpl(database.workoutEntryDao())
    val weightRepository: WeightRepository = WeightRepositoryImpl(database.weightEntryDao())
    val nedovolenAccountRepository: NedovolenAccountRepository =
        NedovolenAccountRepositoryImpl(database.nedovolenAccountDao())
    val stepsRepository: StepsRepository = StepsRepositoryImpl(context, database.stepsBaselineDao())

    val calculateCalorieTargetUseCase = CalculateCalorieTargetUseCase()
}
