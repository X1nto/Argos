package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesSchedule(
    val locationName: String,
    val info: String,
    val lectureTypeName: String,
    val lectureFormatName: String,
)