package dev.xinto.argos.domain.courses

data class DomainCourseChosenGroup(
    val lecturers: List<DomainCourseLecturer>,
    val courseName: String,
    val groupName: String,
)