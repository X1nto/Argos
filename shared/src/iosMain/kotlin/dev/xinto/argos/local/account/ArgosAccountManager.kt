package dev.xinto.argos.local.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class ArgosAccountManager {

    actual fun isLoggedIn(): Flow<Boolean> {
        return callbackFlow {
        }
    }

    actual suspend fun getToken(): String? {
        return suspendCoroutine {
            it.resume("")
        }
    }

    actual suspend fun getRefreshToken(): String? {
        return suspendCoroutine {
            it.resume("")
        }
    }

    actual suspend fun setToken(token: String) {
        suspendCoroutine {
            it.resume(Unit)
        }
    }

    actual suspend fun setRefreshToken(refreshToken: String) {
        suspendCoroutine {
            it.resume(Unit)
        }
    }

    actual suspend fun clearToken() {
        suspendCoroutine {
            it.resume(Unit)
        }
    }

    actual suspend fun clearRefreshToken() {
        suspendCoroutine {
            it.resume(Unit)
        }
    }

    actual suspend fun getProfileId(): String? {
        return suspendCoroutine {
            it.resume("")
        }
    }

    actual suspend fun setProfileId(profileId: String) {
    }

    actual suspend fun clearProfileId() {
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }


}