package dev.xinto.argos.domain.courses

data class DomainCourse(
    val name: String,
    val code: String,
    val programCode: String,
    val credits: Int,
    val degree: Int,
    val isEnabledForChoose: Boolean,
    val isGeneral: Boolean
)