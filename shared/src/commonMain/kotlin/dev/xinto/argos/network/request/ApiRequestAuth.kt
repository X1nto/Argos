package dev.xinto.argos.network.request

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequestAuth(
    val clientId: String,
    val grantType: String,
    val token: String
)
