package dev.xinto.argos.network.request

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequestAuthRefresh(
    val clientId: String,
    val grantType: String,
    val refreshToken: String
)
