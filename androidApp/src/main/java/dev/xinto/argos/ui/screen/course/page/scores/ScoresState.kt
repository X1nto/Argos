package dev.xinto.argos.ui.screen.course.page.scores

import androidx.compose.runtime.Immutable
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
                scores = listOf(
                    "Criteria 1 (min. 1, max. 25)" to 13.5f,
                    "Criteria 2 (min. 10, max. 20)" to 10f,
                    "Criteria 3 (max. 25)" to 0f,
                    "Criteria 4 (min. 1, max. 10)" to 10f,
                )
            )
        )
    }

}