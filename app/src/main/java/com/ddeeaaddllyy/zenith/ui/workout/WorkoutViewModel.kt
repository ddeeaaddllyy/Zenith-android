package com.ddeeaaddllyy.zenith.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.model.WorkoutEntry
import com.ddeeaaddllyy.zenith.domain.model.WorkoutType
import com.ddeeaaddllyy.zenith.domain.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class WorkoutUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<WorkoutEntry> = emptyList(),
    val totalCaloriesBurned: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val entries = selectedDate.flatMapLatest { date ->
        workoutRepository.entriesForDate(date)
    }

    val uiState: StateFlow<WorkoutUiState> = combine(selectedDate, entries) { date, entryList ->
        WorkoutUiState(
            selectedDate = date,
            entries = entryList,
            totalCaloriesBurned = entryList.sumOf { it.caloriesBurned }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkoutUiState())

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

    fun addEntry(type: WorkoutType, durationMinutes: Int, caloriesBurned: Int, note: String?) {
        viewModelScope.launch {
            workoutRepository.addEntry(
                WorkoutEntry(
                    date = selectedDate.value,
                    type = type,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned,
                    note = note
                )
            )
        }
    }

    fun deleteEntry(entry: WorkoutEntry) {
        viewModelScope.launch { workoutRepository.deleteEntry(entry) }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer { WorkoutViewModel(container.workoutRepository) }
        }
    }
}
