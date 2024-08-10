package dev.xinto.argos.local.account

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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

    actual fun isLoggedIn(): Flow<Boolean> {
        return callbackFlow {
            val isLoggedIn = { prefs: SharedPreferences ->
                val token = prefs.getString(KEY_TOKEN, null)
                val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)
                val id = prefs.getString(KEY_ID, null)

                token != null && refreshToken != null && id != null
            }

            trySend(isLoggedIn(securePrefs))

            val callback = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                trySend(isLoggedIn(sharedPreferences))
            }

            securePrefs.registerOnSharedPreferenceChangeListener(callback)
            awaitClose {
                securePrefs.unregisterOnSharedPreferenceChangeListener(callback)
            }
        }
    }

    actual fun logout() {
        securePrefs.edit {
            putString(KEY_TOKEN, null)
            putString(KEY_REFRESH_TOKEN, null)
            putString(KEY_ID, null)
        }
    }

    actual fun getProfileId(): String? = securePrefs.getString(KEY_ID, null)
    actual fun getToken(): String? = securePrefs.getString(KEY_TOKEN, null)
    actual fun getRefreshToken(): String? = securePrefs.getString(KEY_REFRESH_TOKEN, null)

    actual fun setProfileId(profileId: String) {
        securePrefs.edit {
            putString(KEY_ID, profileId)
        }
    }

    actual fun setToken(token: String) {
        securePrefs.edit {
            putString(KEY_TOKEN, token)
        }
    }

    actual fun setRefreshToken(refreshToken: String) {
        securePrefs.edit {
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    private companion object {
        const val KEY_ID = "profile_id"
        const val KEY_TOKEN = "token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}