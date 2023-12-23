package dev.xinto.argos.ui.screen.main.dialog.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.combine
import dev.xinto.argos.domain.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val state = combine(
        userRepository.getUserInfo().asFlow(),
        userRepository.getUserState().asFlow()
    ) { info, state -> info to state }.map {
        when (it) {
            is DomainResponse.Loading -> UserState.Loading
            is DomainResponse.Success -> UserState.Success(
                userInfo = it.value.first,
                userState = it.value.second
            )

            is DomainResponse.Error -> UserState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserState.Loading
    )

}