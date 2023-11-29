package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesSemester(
    val name: String,
    val isActive: Boolean
)