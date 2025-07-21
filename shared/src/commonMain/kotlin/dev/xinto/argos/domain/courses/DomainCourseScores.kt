package dev.xinto.argos.domain.courses

data class DomainCourseScores(
    val requiredCredits: Int,
    val acquiredCredits: Int,
    val criteria: List<DomainCourseCriterion>
)