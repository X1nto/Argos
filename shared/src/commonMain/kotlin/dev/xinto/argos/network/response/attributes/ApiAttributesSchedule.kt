package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesSchedule(
    val roomName: String,
    val info: String,
    val lectureType: String,
)