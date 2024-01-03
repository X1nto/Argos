package dev.xinto.argos.ui.screen.meuserprofile

import dev.xinto.argos.domain.user.DomainMeUserInfo

sealed interface MeUserProfileState {

    data object Loading : MeUserProfileState

    data class Success(val user: DomainMeUserInfo) : MeUserProfileState

    data object Error : MeUserProfileState

}