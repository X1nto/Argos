package dev.xinto.argos.domain.courses

import dev.xinto.argos.util.FormattedLocalDateTime

data class DomainCourseMaterial(
    val id: String,
    val name: String,
    val date: FormattedLocalDateTime,
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
    Video,
    Text
}