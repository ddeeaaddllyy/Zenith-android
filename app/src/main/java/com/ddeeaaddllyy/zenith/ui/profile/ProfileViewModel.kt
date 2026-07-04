package com.ddeeaaddllyy.zenith.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.model.ActivityLevel
import com.ddeeaaddllyy.zenith.domain.model.Goal
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import com.ddeeaaddllyy.zenith.domain.model.WeightEntry
import com.ddeeaaddllyy.zenith.domain.repository.UserRepository
import com.ddeeaaddllyy.zenith.domain.repository.WeightRepository
import com.ddeeaaddllyy.zenith.domain.usecase.CalculateCalorieTargetUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val weightRepository: WeightRepository,
    private val calculateCalorieTarget: CalculateCalorieTargetUseCase
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = userRepository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateWeight(newWeightKg: Double) {
        val current = profile.value ?: return
        viewModelScope.launch {
            weightRepository.addEntry(WeightEntry(date = LocalDate.now(), weightKg = newWeightKg))
            val newTarget = calculateCalorieTarget(
                gender = current.gender,
                age = current.age,
                heightCm = current.heightCm,
                weightKg = newWeightKg,
                activityLevel = current.activityLevel,
                goal = current.goal
            )
            userRepository.saveProfile(
                current.copy(currentWeightKg = newWeightKg, dailyCalorieTarget = newTarget)
            )
        }
    }

    fun updateDetails(
        name: String,
        age: Int,
        heightCm: Int,
        targetWeightKg: Double,
        activityLevel: ActivityLevel,
        goal: Goal
    ) {
        val current = profile.value ?: return
        viewModelScope.launch {
            val newTarget = calculateCalorieTarget(
                gender = current.gender,
                age = age,
                heightCm = heightCm,
                weightKg = current.currentWeightKg,
                activityLevel = activityLevel,
                goal = goal
            )
            userRepository.saveProfile(
                current.copy(
                    name = name,
                    age = age,
                    heightCm = heightCm,
                    targetWeightKg = targetWeightKg,
                    activityLevel = activityLevel,
                    goal = goal,
                    dailyCalorieTarget = newTarget
                )
            )
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                ProfileViewModel(
                    container.userRepository,
                    container.weightRepository,
                    container.calculateCalorieTargetUseCase
                )
            }
        }
    }
}
