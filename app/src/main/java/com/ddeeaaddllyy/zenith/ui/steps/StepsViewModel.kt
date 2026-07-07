package com.ddeeaaddllyy.zenith.ui.steps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.repository.StepsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StepsViewModel(private val stepsRepository: StepsRepository) : ViewModel() {

    val hasStepSensor: Boolean get() = stepsRepository.hasStepSensor

    val todaySteps: StateFlow<Int> = stepsRepository.todaySteps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    companion object {
        const val DAILY_GOAL = 7_000

        fun factory(container: AppContainer) = viewModelFactory {
            initializer { StepsViewModel(container.stepsRepository) }
        }
    }
}
