package dev.xinto.argos.ui.screen.course.page.groups

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.courses.DomainCourseGroup
import dev.xinto.argos.domain.courses.DomainCourseLecturer

@Immutable
sealed interface GroupsState {

    @Immutable
    data object Loading : GroupsState

    @Immutable
    data class Success(val groups: List<DomainCourseGroup>) : GroupsState

    @Immutable
    data object Error : GroupsState

    companion object {
        val mockSuccess = Success(
            groups = buildList {
                repeat(5) {
                    add(
                        DomainCourseGroup(
                            id = "",
                            name = "Group ${it + 1}",
                            isChosen = it == 0,
                            isConflicting = it % 3 == 0,
                            chooseError = if (it % 2 == 0 && it != 0) "" else null,
                            rechooseError = if (it % 2 == 0 && it != 0) "" else null,
                            removeError = if (it % 2 == 0 && it != 0) "" else null,
                            lecturers = listOf(
                                DomainCourseLecturer(
                                    uuid = "",
                                    fullName = "Guram Sherozia",
                                    photoUrl = null
                                )
                            )
                        )
                    )
                }
            }
        )
    }
}