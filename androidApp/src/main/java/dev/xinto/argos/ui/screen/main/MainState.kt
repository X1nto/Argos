package dev.xinto.argos.ui.screen.main

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.user.DomainMeUserInfo
import dev.xinto.argos.domain.user.DomainMeUserState

@Immutable
sealed interface MainState {

    @Immutable
    data object Loading : MainState

    data class Success(
        val userInfo: DomainMeUserInfo,
        val userState: DomainMeUserState
    ) : MainState

    data object Error : MainState
}