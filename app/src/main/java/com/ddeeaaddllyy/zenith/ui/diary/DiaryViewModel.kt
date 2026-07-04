package com.ddeeaaddllyy.zenith.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.model.FoodEntry
import com.ddeeaaddllyy.zenith.domain.repository.NutritionRepository
import com.ddeeaaddllyy.zenith.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DiaryUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<FoodEntry> = emptyList(),
    val totalCalories: Int = 0,
    val calorieTarget: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryViewModel(
    private val nutritionRepository: NutritionRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val entries = selectedDate.flatMapLatest { date ->
        nutritionRepository.entriesForDate(date)
    }

    val uiState: StateFlow<DiaryUiState> = combine(
        selectedDate,
        entries,
        userRepository.profile
    ) { date, entryList, profile ->
        DiaryUiState(
            selectedDate = date,
            entries = entryList,
            totalCalories = entryList.sumOf { it.calories },
            calorieTarget = profile?.dailyCalorieTarget ?: 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DiaryUiState())

    fun onPreviousDay() {
        selectedDate.value = selectedDate.value.minusDays(1)
    }

    fun onNextDay() {
        if (selectedDate.value < LocalDate.now()) {
            selectedDate.value = selectedDate.value.plusDays(1)
        }
    }

    fun selectDate(date: LocalDate) {
        if (!date.isAfter(LocalDate.now())) {
            selectedDate.value = date
        }
    }

    fun addEntry(name: String, calories: Int, proteinG: Double?, fatG: Double?, carbsG: Double?) {
        viewModelScope.launch {
            nutritionRepository.addEntry(
                FoodEntry(
                    date = selectedDate.value,
                    name = name,
                    calories = calories,
                    proteinG = proteinG,
                    fatG = fatG,
                    carbsG = carbsG
                )
            )
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch { nutritionRepository.deleteEntry(entry) }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                DiaryViewModel(container.nutritionRepository, container.userRepository)
            }
        }
    }
}
