package dev.xinto.argos.ui.screen.course.page.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.courses.CoursesRepository
import dev.xinto.argos.domain.courses.DomainCourseGroupSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsViewModel(
    savedStateHandle: SavedStateHandle,
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    private val courseId: String = savedStateHandle[KEY_COURSE_ID]!!
    private val courses = coursesRepository.getCourseGroups(courseId)

    val state = courses
        .asFlow()
        .map {
            when (it) {
                is DomainResponse.Loading -> GroupsState.Loading
                is DomainResponse.Success -> GroupsState.Success(it.value)
                is DomainResponse.Error -> GroupsState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GroupsState.Loading
        )

    private val _schedules =
        MutableStateFlow<Map<String, DomainResponse<List<DomainCourseGroupSchedule>>>>(mapOf())
    val schedules = _schedules.asStateFlow()

    private val _expanded = MutableStateFlow<Set<String>>(setOf())
    val expanded = _expanded.asStateFlow()

    fun setExpanded(groupId: String, expanded: Boolean) {
        _expanded.update {
            if (expanded) {
                it + groupId
            } else {
                it - groupId
            }
        }

        if (_schedules.value[groupId] != null) return

        viewModelScope.launch {
            _schedules.update {
                it.toMutableMap().apply {
                    this[groupId] = coursesRepository.getCourseGroupSchedule(courseId, groupId)()
                }
            }
        }
    }

    fun refresh() {
        courses.refresh()
        _expanded.update { emptySet() }
        _schedules.update { emptyMap() }
    }

    companion object {
        const val KEY_COURSE_ID = "course"
    }

}