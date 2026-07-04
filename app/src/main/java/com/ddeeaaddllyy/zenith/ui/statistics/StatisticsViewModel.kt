package com.ddeeaaddllyy.zenith.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.repository.NutritionRepository
import com.ddeeaaddllyy.zenith.domain.repository.UserRepository
import com.ddeeaaddllyy.zenith.domain.repository.WeightRepository
import com.ddeeaaddllyy.zenith.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth

data class DayCalories(val date: LocalDate, val calories: Int)
data class WeightPoint(val date: LocalDate, val weightKg: Double)
data class MonthlyWorkoutStat(val month: YearMonth, val count: Int)

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val calorieTarget: Int = 0,
    val caloriesToday: Int = 0,
    val caloriesRemaining: Int = 0,
    val workoutsToday: Int = 0,
    val caloriesBurnedToday: Int = 0,
    val last7DaysCalories: List<DayCalories> = emptyList(),
    val workoutsThisWeek: Int = 0,
    val caloriesBurnedThisWeek: Int = 0,
    val startWeightKg: Double = 0.0,
    val currentWeightKg: Double = 0.0,
    val targetWeightKg: Double = 0.0,
    val weightChangeKg: Double = 0.0,
    val weightHistory: List<WeightPoint> = emptyList(),
    val monthlyWorkoutStats: List<MonthlyWorkoutStat> = emptyList()
)

class StatisticsViewModel(
    userRepository: UserRepository,
    nutritionRepository: NutritionRepository,
    workoutRepository: WorkoutRepository,
    weightRepository: WeightRepository
) : ViewModel() {

    private val today = LocalDate.now()
    private val weekStart = today.minusDays(6)
    private val monthsWindowStart = YearMonth.from(today).minusMonths(5).atDay(1)

    val uiState: StateFlow<StatisticsUiState> = combine(
        userRepository.profile,
        nutritionRepository.entriesBetween(weekStart, today),
        workoutRepository.entriesBetween(weekStart, today),
        weightRepository.entries,
        workoutRepository.entriesBetween(monthsWindowStart, today)
    ) { profile, foodEntries, workoutEntries, weightEntries, monthlyWorkoutEntries ->
        if (profile == null) {
            StatisticsUiState(isLoading = true)
        } else {
            val caloriesByDay = (0..6).map { offset ->
                val date = weekStart.plusDays(offset.toLong())
                val total = foodEntries.filter { it.date == date }.sumOf { it.calories }
                DayCalories(date, total)
            }
            val todaysWorkouts = workoutEntries.filter { it.date == today }
            val startMonth = YearMonth.from(monthsWindowStart)
            val monthlyStats = (0..5).map { offset ->
                val month = startMonth.plusMonths(offset.toLong())
                val count = monthlyWorkoutEntries.count { YearMonth.from(it.date) == month }
                MonthlyWorkoutStat(month, count)
            }

            StatisticsUiState(
                isLoading = false,
                calorieTarget = profile.dailyCalorieTarget,
                caloriesToday = caloriesByDay.last().calories,
                caloriesRemaining = profile.dailyCalorieTarget - caloriesByDay.last().calories,
                workoutsToday = todaysWorkouts.size,
                caloriesBurnedToday = todaysWorkouts.sumOf { it.caloriesBurned },
                last7DaysCalories = caloriesByDay,
                workoutsThisWeek = workoutEntries.size,
                caloriesBurnedThisWeek = workoutEntries.sumOf { it.caloriesBurned },
                startWeightKg = profile.startWeightKg,
                currentWeightKg = profile.currentWeightKg,
                targetWeightKg = profile.targetWeightKg,
                weightChangeKg = profile.weightChangeKg,
                weightHistory = weightEntries.map { WeightPoint(it.date, it.weightKg) }.takeLast(14),
                monthlyWorkoutStats = monthlyStats
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsUiState())

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                StatisticsViewModel(
                    container.userRepository,
                    container.nutritionRepository,
                    container.workoutRepository,
                    container.weightRepository
                )
            }
        }
    }
}
