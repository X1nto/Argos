package dev.xinto.argos.ui.screen.course.page.syllabus

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.courses.DomainCourseSyllabus

@Immutable
sealed interface SyllabusState {

    @Immutable
    data object Loading : SyllabusState

    @Immutable
    data class Success(
        val syllabus: DomainCourseSyllabus
    ) : SyllabusState

    @Immutable
    data object Error : SyllabusState

}