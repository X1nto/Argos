package dev.xinto.argos.domain.lectures

data class DomainLectureInfo(
    val id: String,
    val time: String,
    val room: String,
    val name: String,
    val lecturer: String,
)