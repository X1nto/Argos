package dev.xinto.argos.ui.screen.user

import dev.xinto.argos.domain.user.DomainUserInfo

sealed interface UserState {

    data object Loading : UserState

    data class Success(val user: DomainUserInfo) : UserState

    data object Error : UserState

}