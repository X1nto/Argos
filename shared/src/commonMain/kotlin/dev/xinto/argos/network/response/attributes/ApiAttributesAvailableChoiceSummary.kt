package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesAvailableChoiceSummary(
    val creditsAll: Int,
    val creditsCurrent: Int,
    val creditsGeneral: Int
)