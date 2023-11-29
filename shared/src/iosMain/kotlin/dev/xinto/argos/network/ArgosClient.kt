package dev.xinto.argos.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val ArgosApi.Companion.engine: HttpClientEngineFactory<*>
    get() = Darwin