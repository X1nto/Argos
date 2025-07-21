package dev.xinto.argos.ui.screen.course.page.scores

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.courses.DomainCourseCriterion
import dev.xinto.argos.domain.courses.DomainCourseScores

@Immutable
sealed interface ScoresState {

    @Immutable
    data object Loading : ScoresState

    @Immutable
    data class Success(val scores: DomainCourseScores) : ScoresState

    @Immutable
    data object Error : ScoresState

    companion object {
        val mockSuccess = Success(
            DomainCourseScores(
                requiredCredits = 8,
                acquiredCredits = 0,
                criteria = listOf(
                    DomainCourseCriterion("Criteria 1 (min. 1, max. 25)", 13.5f),
                    DomainCourseCriterion("Criteria 2 (min. 10, max. 20)", 10f),
                    DomainCourseCriterion("Criteria 3 (max. 25)", 0f),
                    DomainCourseCriterion("Criteria 4 (min. 1, max. 10)",  10f),
                )
            )
        )
    }

}