package dev.xinto.argos.domain.courses

data class DomainCourseGroup(
    val id: String,
    val name: String,
    val isChosen: Boolean,
    val isConflicting: Boolean,
    val chooseError: String?,
    val rechooseError: String?,
    val removeError: String?,
    val lecturers: List<DomainCourseLecturer>
)