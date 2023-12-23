package dev.xinto.argos.ui.screen.course.page.classmates

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.courses.DomainCourseClassmate

@Immutable
sealed interface ClassmatesState {

    @Immutable
    data object Loading : ClassmatesState

    @Immutable
    data class Success(val classmates: List<DomainCourseClassmate>) : ClassmatesState

    @Immutable
    data object Error : ClassmatesState

    companion object {
        val mockSuccess = Success(
            classmates = listOf(
                DomainCourseClassmate(
                    uuid = "",
                    fullName = "Guram Sherozia",
                    photoUrl = null
                )
            )
        )
    }
}