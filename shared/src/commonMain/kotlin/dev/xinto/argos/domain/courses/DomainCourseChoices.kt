package dev.xinto.argos.domain.courses

data class DomainCourseChoices(
    val allCredits: Int,
    val currentCredits: Int,
    val generalCredits: Int,
    val course: List<DomainMyCourse>
)