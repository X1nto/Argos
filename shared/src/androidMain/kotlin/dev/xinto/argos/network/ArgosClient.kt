package dev.xinto.argos.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual val ArgosApi.Companion.engine: HttpClientEngineFactory<*>
    get() = Android