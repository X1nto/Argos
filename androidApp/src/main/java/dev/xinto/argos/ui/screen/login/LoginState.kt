package dev.xinto.argos.ui.screen.login

import androidx.compose.runtime.Immutable

@Immutable
sealed interface LoginState {

    @Immutable
    data object Stale : LoginState

    @Immutable
    data object Loading : LoginState

    @Immutable
    data object Error : LoginState
}