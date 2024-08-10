package dev.xinto.argos.local.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class ArgosSettings {

    actual fun observeLanguage(): Flow<ArgosLanguage> = flowOf(ArgosLanguage.Ka)

    actual suspend fun getLanguage(): ArgosLanguage = ArgosLanguage.Ka

    actual suspend fun setLanguage(language: ArgosLanguage) {}

}