package dev.xinto.argos.ui.screen.main.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.lectures.LecturesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HomeViewModel(
    private val lecturesRepository: LecturesRepository
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(LocalDate.now().dayOfWeek.value - 1)

    val state = combine(lecturesRepository.observeLectures(), _selectedDay) { lectures, selectedDay ->
        when (lectures) {
            is DomainResponse.Loading -> HomeState.Loading
            is DomainResponse.Success -> {
                val selectedIndex = if (selectedDay > lectures.value.size) 0 else selectedDay
                HomeState.Success(
                    selectedDay = selectedIndex,
                    lectures = lectures.value
                )
            }
            is DomainResponse.Error -> HomeState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState.Loading
    )

    fun selectDay(index: Int) {
        _selectedDay.value = index
    }
}