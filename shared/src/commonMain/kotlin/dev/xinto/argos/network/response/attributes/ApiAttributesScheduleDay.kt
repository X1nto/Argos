package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesScheduleDay(
    val number: Int,
    val name: String
)
