package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesAuth(
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)
