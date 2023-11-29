package dev.xinto.argos.local.settings

import kotlinx.coroutines.flow.Flow

expect class ArgosSettings {

    fun observeLanguage(): Flow<ArgosLanguage>

    suspend fun getLanguage(): ArgosLanguage

    suspend fun setLanguage(language: ArgosLanguage)

}