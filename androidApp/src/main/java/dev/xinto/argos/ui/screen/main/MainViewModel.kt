package dev.xinto.argos.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.combine
import dev.xinto.argos.domain.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val meUserInfo = userRepository.getMeUserInfo()
    private val meUserState = userRepository.getMeUserState()

    val state = combine(meUserInfo.asFlow(), meUserState.asFlow()) { info, state ->
        info to state
    }.map {
        when (it) {
            is DomainResponse.Loading -> MainState.Loading
            is DomainResponse.Success -> MainState.Success(
                userInfo = it.value.first,
                userState = it.value.second
            )
            is DomainResponse.Error -> MainState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainState.Loading
    )

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}