package dev.xinto.argos.ui.screen.course.page.classmates

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.courses.CoursesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClassmatesViewModel(
    savedStateHandle: SavedStateHandle,
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    private val classmates =
        coursesRepository.getCourseClassmates(savedStateHandle[KEY_COURSE_ID]!!)

    val state = classmates
        .flow
        .map {
            when (it) {
                is DomainResponse.Loading -> ClassmatesState.Loading
                is DomainResponse.Success -> ClassmatesState.Success(it.value)
                is DomainResponse.Error -> ClassmatesState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ClassmatesState.Loading
        )

    fun refresh() {
        classmates.refresh()
    }

    companion object {
        const val KEY_COURSE_ID = "course"
    }
}