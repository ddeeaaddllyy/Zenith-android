package com.ddeeaaddllyy.zenith.data.repository

import com.ddeeaaddllyy.zenith.data.local.dao.WeightEntryDao
import com.ddeeaaddllyy.zenith.data.local.entity.WeightEntryEntity
import com.ddeeaaddllyy.zenith.domain.model.WeightEntry
import com.ddeeaaddllyy.zenith.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WeightRepositoryImpl(private val dao: WeightEntryDao) : WeightRepository {

    override val entries: Flow<List<WeightEntry>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addEntry(entry: WeightEntry) {
        dao.insert(entry.toEntity())
    }

    private fun WeightEntryEntity.toDomain() = WeightEntry(
        id = id,
        date = LocalDate.parse(date),
        weightKg = weightKg
    )

    private fun WeightEntry.toEntity() = WeightEntryEntity(
        id = id,
        date = date.toString(),
        weightKg = weightKg
    )
}
