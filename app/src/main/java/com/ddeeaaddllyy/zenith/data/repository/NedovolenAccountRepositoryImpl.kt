package com.ddeeaaddllyy.zenith.data.repository

import com.ddeeaaddllyy.zenith.data.local.dao.NedovolenAccountDao
import com.ddeeaaddllyy.zenith.data.local.entity.NedovolenAccountEntity
import com.ddeeaaddllyy.zenith.data.security.PasswordHasher
import com.ddeeaaddllyy.zenith.domain.model.LoginResult
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

    override suspend fun register(login: String, password: String) {
        val salt = PasswordHasher.generateSalt()
        dao.upsert(
            NedovolenAccountEntity(
                login = login,
                passwordHash = PasswordHasher.hash(password, salt),
                passwordSalt = salt,
                isLoggedIn = true,
                loggedInAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun login(login: String, password: String): LoginResult {
        val existing = dao.get() ?: return LoginResult.NoAccount
        if (existing.login != login) return LoginResult.InvalidCredentials
        val hash = PasswordHasher.hash(password, existing.passwordSalt)
        if (hash != existing.passwordHash) return LoginResult.InvalidCredentials
        dao.setLoggedIn(System.currentTimeMillis())
        return LoginResult.Success
    }

    override suspend fun logout() {
        dao.setLoggedOut()
    }
}
