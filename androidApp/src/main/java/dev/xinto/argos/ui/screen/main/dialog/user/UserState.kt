package dev.xinto.argos.ui.screen.main.dialog.user

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.user.DomainUserInfo
import dev.xinto.argos.domain.user.DomainUserState

@Immutable
sealed interface UserState {

    @Immutable
    data object Loading : UserState

    @Immutable
    data class Success(
        val userInfo: DomainUserInfo,
        val userState: DomainUserState
    ) : UserState

    @Immutable
    data object Error : UserState

    companion object {

        val mockSuccess = Success(
            userInfo = DomainUserInfo(
                fullName = "Giorgi Giorgadze",
                email = "giorgi.giorgadze.1@iliauni.edu.ge",
                photoUrl = null,
                degree = 1
            ),
            userState = DomainUserState(
                billingBalance = "562.50₾",
                libraryBalance = "0",
                newsUnread = 0,
                messagesUnread = 0,
                notificationsUnread = 0
            )
        )

    }

}