package dev.xinto.argos.domain.courses

data class DomainCourseGroupSchedule(
    val day: String,
    val time: String,
    val room: String,
    val info: String?
)