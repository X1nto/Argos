package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesUserProfileStudent(
    val type: Int,
    val degree: Int
)