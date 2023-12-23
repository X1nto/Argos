package dev.xinto.argos.domain.courses

import kotlinx.datetime.LocalDateTime

data class DomainCourseMaterial(
    val id: String,
    val name: String,
    val date: LocalDateTime,
    val lecturer: DomainCourseLecturer,
    val size: Long,
    val downloadUrl: String,
    val type: DomainCourseMaterialType
)

enum class DomainCourseMaterialType {
    Word,
    Excel,
    Powerpoint,
    Pdf,
    Text
}