package dev.xinto.argos.domain.courses

data class DomainCourseScores(
    val requiredCredits: Int,
    val acquiredCredits: Int,
    val scores: List<Pair<String, Float>>
)