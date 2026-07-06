package com.ddeeaaddllyy.zenith.domain.repository

import com.ddeeaaddllyy.zenith.domain.model.NedovolenSession
import kotlinx.coroutines.flow.Flow

/**
 * "Nedovolen" is the shared account system across all of this developer's apps.
 * For now this is local-only — a future POST API will sync the session to the real backend.
 */
interface NedovolenAccountRepository {
    val session: Flow<NedovolenSession?>
    suspend fun login(login: String)
    suspend fun logout()
}
