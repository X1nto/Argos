package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesUserProfile(
    val type: Int,
    val degree: Int
)