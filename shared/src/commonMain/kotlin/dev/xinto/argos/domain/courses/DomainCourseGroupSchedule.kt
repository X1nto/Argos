package dev.xinto.argos.domain.courses

data class DomainCourseGroupSchedule(
    val id: String,
    val date: String,
    val day: String,
    val startTime: String,
    val fullTime: String,
    val room: String,
    val lecturer: String
)