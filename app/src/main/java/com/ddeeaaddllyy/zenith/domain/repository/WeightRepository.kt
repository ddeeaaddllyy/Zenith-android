package com.ddeeaaddllyy.zenith.domain.repository

import com.ddeeaaddllyy.zenith.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    val entries: Flow<List<WeightEntry>>
    suspend fun addEntry(entry: WeightEntry)
}
