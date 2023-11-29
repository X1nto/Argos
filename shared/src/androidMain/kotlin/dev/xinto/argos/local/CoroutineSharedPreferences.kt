package dev.xinto.argos.local

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CoroutineSharedPreferences(preferences: SharedPreferences) : SharedPreferences by preferences {

    fun observeString(key: String, default: String?): Flow<String?> {
        return observeValue {
            it.getString(key, default)
        }
    }

    fun observeInt(key: String, default: Int): Flow<Int> {
        return observeValue {
            it.getInt(key, default)
        }
    }

    suspend inline fun edit(crossinline block: SharedPreferences.Editor.() -> Unit): Boolean {
        return suspendCoroutine {
            it.resume(edit().apply(block).commit())
        }
    }

    inline fun <reified E: Enum<E>> observeEnum(key: String, default: E): Flow<E> {
        return observeValue {
            it.getEnum(key, default)
        }
    }

    inline fun <reified T> observeValue(crossinline value: (SharedPreferences) -> T): Flow<T> {
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, _ ->
                trySendBlocking(value(prefs))
            }
            send(value(this@CoroutineSharedPreferences))
            registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }
}

inline fun <reified E: Enum<E>> SharedPreferences.getEnum(key: String, default: E): E {
    val name = getString(key, default.name)!!
    return enumValueOf(name)
}

inline fun <reified E: Enum<E>> SharedPreferences.Editor.putEnum(key: String, value: E) {
    putString(key, value.name)
}