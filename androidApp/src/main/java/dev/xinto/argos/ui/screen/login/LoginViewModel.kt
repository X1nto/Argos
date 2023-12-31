package dev.xinto.argos.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.user.UserRepository
import dev.xinto.argos.ui.component.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Stale)
    val state = _state.asStateFlow()

    fun login(authResult: AuthResult) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            when (authResult) {
                is AuthResult.Success -> {
                    if (!userRepository.login(authResult.idToken!!)) {
                        _state.value = LoginState.Error
                    }
                }
                is AuthResult.Error -> {
                    _state.value = LoginState.Error
                }
            }
        }
    }

}