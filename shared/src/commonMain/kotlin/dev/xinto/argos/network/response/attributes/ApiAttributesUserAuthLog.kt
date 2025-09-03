package dev.xinto.argos.network.response.attributes

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesUserAuthLog(
    val ip: String,
    val userAgent: String,
    val createdAt: Instant
)