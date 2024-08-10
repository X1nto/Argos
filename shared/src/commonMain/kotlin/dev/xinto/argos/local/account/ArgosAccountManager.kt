package dev.xinto.argos.local.account

import kotlinx.coroutines.flow.Flow

expect class ArgosAccountManager {

    fun isLoggedIn(): Flow<Boolean>

    fun logout()

    fun getProfileId(): String?
    fun getToken(): String?
    fun getRefreshToken(): String?

    fun setProfileId(profileId: String)
    fun setToken(token: String)
    fun setRefreshToken(refreshToken: String)

}