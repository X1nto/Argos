package dev.xinto.argos.ui.screen.userprofile

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.user.DomainUserProfile

@Immutable
sealed interface UserProfileState {

    @Immutable
    data object Loading : UserProfileState

    @Immutable
    data class Success(val profile: DomainUserProfile) : UserProfileState

    @Immutable
    data object Error : UserProfileState

}