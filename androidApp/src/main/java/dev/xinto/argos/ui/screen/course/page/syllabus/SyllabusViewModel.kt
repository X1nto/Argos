package dev.xinto.argos.ui.screen.course.page.syllabus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.courses.CoursesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SyllabusViewModel(
    savedStateHandle: SavedStateHandle,
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    private val syllabus = coursesRepository.getCourseSyllabus(savedStateHandle[KEY_COURSE_ID]!!)

    val state = syllabus
        .asFlow()
        .map {
            when (it) {
                is DomainResponse.Loading -> SyllabusState.Loading
                is DomainResponse.Success -> SyllabusState.Success(it.value)
                is DomainResponse.Error -> SyllabusState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SyllabusState.Loading
        )


    companion object {
        const val KEY_COURSE_ID = "course"
    }

}