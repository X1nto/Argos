package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesScore(
    val criteria: String,
    val score: Float
)
