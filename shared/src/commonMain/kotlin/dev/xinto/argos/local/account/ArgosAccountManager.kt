package dev.xinto.argos.local.account

import kotlinx.coroutines.flow.Flow

expect class ArgosAccountManager {

    fun isLoggedIn(): Flow<Boolean>

    suspend fun logout()

    suspend fun getProfileId(): String?
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?

    suspend fun setProfileId(profileId: String)
    suspend fun setToken(token: String)
    suspend fun setRefreshToken(refreshToken: String)

}