package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesGroup(
    val name: String,
    val maxQuota: Int
)