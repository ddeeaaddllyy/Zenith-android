package com.ddeeaaddllyy.zenith.domain.repository

import com.ddeeaaddllyy.zenith.domain.model.AppTheme
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val profile: Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun updateTheme(theme: AppTheme)
}
