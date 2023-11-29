package dev.xinto.argos.ui.screen.main

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.user.DomainUserInfo
import dev.xinto.argos.domain.user.DomainUserState

@Immutable
sealed interface MainState {

    @Immutable
    data object Loading : MainState

    data class Success(
        val userInfo: DomainUserInfo,
        val userState: DomainUserState
    ) : MainState

    data object Error : MainState
}