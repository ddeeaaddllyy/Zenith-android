package com.ddeeaaddllyy.zenith.data.repository

import com.ddeeaaddllyy.zenith.data.local.dao.NedovolenAccountDao
import com.ddeeaaddllyy.zenith.data.local.entity.NedovolenAccountEntity
import com.ddeeaaddllyy.zenith.domain.model.NedovolenSession
import com.ddeeaaddllyy.zenith.domain.repository.NedovolenAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NedovolenAccountRepositoryImpl(private val dao: NedovolenAccountDao) : NedovolenAccountRepository {

    override val session: Flow<NedovolenSession?> = dao.observeSession().map { entity ->
        if (entity != null && entity.isLoggedIn) {
            NedovolenSession(login = entity.login, loggedInAt = entity.loggedInAt)
        } else {
            null
        }
    }

    override suspend fun login(login: String) {
        dao.upsert(
            NedovolenAccountEntity(
                login = login,
                isLoggedIn = true,
                loggedInAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun logout() {
        dao.clear()
    }
}
