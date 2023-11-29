package dev.xinto.argos.local.settings

import android.content.Context
import dev.xinto.argos.local.CoroutineSharedPreferences
import dev.xinto.argos.local.getEnum
import dev.xinto.argos.local.putEnum
import kotlinx.coroutines.flow.Flow

actual class ArgosSettings(context: Context) {

    private val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val coroutinePreferences = CoroutineSharedPreferences(preferences)

    actual fun observeLanguage(): Flow<ArgosLanguage> {
        return coroutinePreferences.observeEnum(KEY_LANGUAGE, ArgosLanguage.Ka)
    }

    actual suspend fun getLanguage(): ArgosLanguage {
        return coroutinePreferences.getEnum(KEY_LANGUAGE, ArgosLanguage.Ka)
    }

    actual suspend fun setLanguage(language: ArgosLanguage) {
        coroutinePreferences.edit { putEnum(KEY_LANGUAGE, language) }
    }

    private companion object {
        const val KEY_LANGUAGE = "language"
    }

}