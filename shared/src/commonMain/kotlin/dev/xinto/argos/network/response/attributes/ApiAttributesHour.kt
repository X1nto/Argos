package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesHour(
    val startTime: String,
    val endTime: String,
    val times: String,
    val day: String
)