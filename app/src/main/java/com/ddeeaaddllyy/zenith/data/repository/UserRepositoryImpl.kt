package com.ddeeaaddllyy.zenith.data.repository

import com.ddeeaaddllyy.zenith.data.local.dao.UserProfileDao
import com.ddeeaaddllyy.zenith.data.local.entity.UserProfileEntity
import com.ddeeaaddllyy.zenith.domain.model.ActivityLevel
import com.ddeeaaddllyy.zenith.domain.model.Gender
import com.ddeeaaddllyy.zenith.domain.model.Goal
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import com.ddeeaaddllyy.zenith.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(private val dao: UserProfileDao) : UserRepository {

    override val profile: Flow<UserProfile?> =
        dao.observeProfile().map { it?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        name = name,
        gender = Gender.valueOf(gender),
        age = age,
        heightCm = heightCm,
        startWeightKg = startWeightKg,
        currentWeightKg = currentWeightKg,
        targetWeightKg = targetWeightKg,
        activityLevel = ActivityLevel.valueOf(activityLevel),
        goal = Goal.valueOf(goal),
        dailyCalorieTarget = dailyCalorieTarget
    )

    private fun UserProfile.toEntity() = UserProfileEntity(
        name = name,
        gender = gender.name,
        age = age,
        heightCm = heightCm,
        startWeightKg = startWeightKg,
        currentWeightKg = currentWeightKg,
        targetWeightKg = targetWeightKg,
        activityLevel = activityLevel.name,
        goal = goal.name,
        dailyCalorieTarget = dailyCalorieTarget
    )
}
