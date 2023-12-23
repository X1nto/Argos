package dev.xinto.argos.ui.screen.course.page.scores

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.courses.CoursesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ScoresViewModel(
    savedStateHandle: SavedStateHandle,
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    private val course = coursesRepository.getCourseScores(savedStateHandle[KEY_COURSE_ID]!!)

    val state = course
        .asFlow()
        .map {
            when (it) {
                is DomainResponse.Loading -> ScoresState.Loading
                is DomainResponse.Success -> ScoresState.Success(it.value)
                is DomainResponse.Error -> ScoresState.Error
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScoresState.Loading
        )

    fun refresh() {
        course.refresh()
    }

    companion object {
        const val KEY_COURSE_ID = "course"
    }
}