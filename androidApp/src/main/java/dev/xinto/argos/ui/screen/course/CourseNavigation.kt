package dev.xinto.argos.ui.screen.course

sealed interface CourseNavigation {

    data object Syllabus : CourseNavigation

    data object Groups : CourseNavigation

    data object Scores : CourseNavigation

    data object Materials : CourseNavigation

    data object Classmates : CourseNavigation

    companion object {
        val tabs by lazy {
            setOf(Syllabus, Groups, Scores, Materials, Classmates)
        }
    }

}