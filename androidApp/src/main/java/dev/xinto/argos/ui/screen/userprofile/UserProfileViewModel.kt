package dev.xinto.argos.ui.screen.userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserProfileViewModel(
    savedStateHandle: SavedStateHandle,
    userRepository: UserRepository
) : ViewModel() {

    private val profile = userRepository.getUserProfile(savedStateHandle[KEY_USER_ID]!!)

    val state = profile
        .asFlow()
        .map {
            when (it) {
                is DomainResponse.Loading -> UserProfileState.Loading
                is DomainResponse.Success -> UserProfileState.Success(it.value)
                is DomainResponse.Error -> UserProfileState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileState.Loading
        )

    fun refresh() {
        profile.refresh()
    }

    companion object {
        const val KEY_USER_ID = "user"
    }
}