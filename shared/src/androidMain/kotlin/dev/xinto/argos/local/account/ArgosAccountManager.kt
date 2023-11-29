package dev.xinto.argos.local.account

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import dev.xinto.argos.local.CoroutineSharedPreferences
import kotlinx.coroutines.flow.Flow

actual class ArgosAccountManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.setKeyGenParameterSpec(MasterKeys.AES256_GCM_SPEC)
            } else {
                it.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            }
        }
        .build()

    private val securePrefs = EncryptedSharedPreferences.create(
        context,
        "account",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val coroutineSecurePrefs = CoroutineSharedPreferences(securePrefs)

    actual fun isLoggedIn(): Flow<Boolean> {
        return coroutineSecurePrefs.observeValue {
            it.getString(KEY_TOKEN, null) != null &&
                    it.getString(KEY_REFRESH_TOKEN, null) != null &&
                    it.getString(KEY_ID, null) != null
        }
    }

    actual suspend fun getProfileId(): String? = coroutineSecurePrefs.getString(KEY_ID, null)
    actual suspend fun getToken(): String? = coroutineSecurePrefs.getString(KEY_TOKEN, null)
    actual suspend fun getRefreshToken(): String? = coroutineSecurePrefs.getString(KEY_REFRESH_TOKEN, null)

    actual suspend fun setProfileId(profileId: String) {
        coroutineSecurePrefs.edit {
            putString(KEY_ID, profileId)
        }
    }

    actual suspend fun setToken(token: String) {
        coroutineSecurePrefs.edit {
            putString(KEY_TOKEN, token)
        }
    }

    actual suspend fun setRefreshToken(refreshToken: String) {
        coroutineSecurePrefs.edit {
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    actual suspend fun clearProfileId() {
        coroutineSecurePrefs.edit {
            putString(KEY_ID, null)
        }
    }

    actual suspend fun clearToken() {
        coroutineSecurePrefs.edit {
            putString(KEY_TOKEN, null)
        }
    }

    actual suspend fun clearRefreshToken() {
        coroutineSecurePrefs.edit {
            putString(KEY_REFRESH_TOKEN, null)
        }
    }

    private companion object {
        const val KEY_ID = "profile_id"
        const val KEY_TOKEN = "token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}