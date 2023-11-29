package dev.xinto.argos.domain.auth

import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.network.ArgosApi
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val argosApi: ArgosApi,
    private val argosAccountManager: ArgosAccountManager
) {

    suspend fun login(googleIdToken: String): Boolean {
        return argosApi.loginGoogle(googleIdToken)
    }

    fun observeLoggedIn(): Flow<Boolean> {
        return argosAccountManager.isLoggedIn()
    }
}