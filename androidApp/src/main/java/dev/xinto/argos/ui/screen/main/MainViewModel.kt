package dev.xinto.argos.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val state = userRepository.observeUser()
        .map {
            when (it) {
                is DomainResponse.Loading -> MainState.Loading
                is DomainResponse.Success -> MainState.Success(it.value.first, it.value.second)
                is DomainResponse.Error -> MainState.Error
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainState.Loading
        )

}