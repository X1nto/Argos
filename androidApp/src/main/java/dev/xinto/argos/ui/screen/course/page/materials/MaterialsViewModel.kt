package dev.xinto.argos.ui.screen.course.page.materials

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.xinto.argos.domain.courses.CoursesRepository

class MaterialsViewModel(
    savedStateHandle: SavedStateHandle,
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    private val materials =
        coursesRepository.getCourseGroupMaterials(savedStateHandle[KEY_COURSE_ID]!!)

    val state = materials
        .flow
        .cachedIn(viewModelScope)

    companion object {
        const val KEY_COURSE_ID = "course"
    }

}