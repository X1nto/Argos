package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesGroupSchedule(
    val day: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val locationName: String,
    val info: String,
    val lectureType: String,
    val scheduleConflictMessage: String?
)
