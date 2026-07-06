package com.ddeeaaddllyy.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepsRepository {
    val hasStepSensor: Boolean
    val todaySteps: Flow<Int>
}
