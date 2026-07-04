package com.ddeeaaddllyy.zenith.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.model.ActivityLevel
import com.ddeeaaddllyy.zenith.domain.model.Gender
import com.ddeeaaddllyy.zenith.domain.model.Goal
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import com.ddeeaaddllyy.zenith.domain.repository.UserRepository
import com.ddeeaaddllyy.zenith.domain.usecase.CalculateCalorieTargetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val targetWeightKg: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val goal: Goal = Goal.MAINTAIN,
    val isSaving: Boolean = false
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
            (age.toIntOrNull() ?: 0) in 10..100 &&
            (heightCm.toIntOrNull() ?: 0) in 100..250 &&
            (weightKg.toDoubleOrNull() ?: 0.0) in 30.0..300.0 &&
            (targetWeightKg.toDoubleOrNull() ?: 0.0) in 30.0..300.0
}

class OnboardingViewModel(
    private val userRepository: UserRepository,
    private val calculateCalorieTarget: CalculateCalorieTargetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onGenderChange(value: Gender) = _uiState.update { it.copy(gender = value) }
    fun onAgeChange(value: String) = _uiState.update { it.copy(age = value.filter { c -> c.isDigit() }) }
    fun onHeightChange(value: String) = _uiState.update { it.copy(heightCm = value.filter { c -> c.isDigit() }) }
    fun onWeightChange(value: String) = _uiState.update { it.copy(weightKg = value.filterDecimal()) }
    fun onTargetWeightChange(value: String) = _uiState.update { it.copy(targetWeightKg = value.filterDecimal()) }
    fun onActivityLevelChange(value: ActivityLevel) = _uiState.update { it.copy(activityLevel = value) }
    fun onGoalChange(value: Goal) = _uiState.update { it.copy(goal = value) }

    fun submit(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.isValid || state.isSaving) return

        val age = state.age.toInt()
        val height = state.heightCm.toInt()
        val weight = state.weightKg.toDouble()
        val targetWeight = state.targetWeightKg.toDouble()

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val calorieTarget = calculateCalorieTarget(
                gender = state.gender,
                age = age,
                heightCm = height,
                weightKg = weight,
                activityLevel = state.activityLevel,
                goal = state.goal
            )
            userRepository.saveProfile(
                UserProfile(
                    name = state.name.trim(),
                    gender = state.gender,
                    age = age,
                    heightCm = height,
                    startWeightKg = weight,
                    currentWeightKg = weight,
                    targetWeightKg = targetWeight,
                    activityLevel = state.activityLevel,
                    goal = state.goal,
                    dailyCalorieTarget = calorieTarget
                )
            )
            _uiState.update { it.copy(isSaving = false) }
            onDone()
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                OnboardingViewModel(
                    container.userRepository,
                    container.calculateCalorieTargetUseCase
                )
            }
        }
    }
}

private fun String.filterDecimal(): String =
    filterIndexed { index, c -> c.isDigit() || (c == '.' && !this.take(index).contains('.')) }
