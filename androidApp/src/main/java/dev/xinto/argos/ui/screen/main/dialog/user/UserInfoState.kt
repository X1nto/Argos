package dev.xinto.argos.ui.screen.main.dialog.user

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.user.DomainMeUserInfo
import dev.xinto.argos.domain.user.DomainMeUserState

@Immutable
sealed interface UserInfoState {

    @Immutable
    data object Loading : UserInfoState

    @Immutable
    data class Success(
        val userInfo: DomainMeUserInfo,
        val userState: DomainMeUserState
    ) : UserInfoState

    @Immutable
    data object Error : UserInfoState

    companion object {

        val mockSuccess = Success(
            userInfo = DomainMeUserInfo(
                fullName = "Giorgi Giorgadze",
                firstName = "Giorgi",
                lastName = "Giorgadze",
                birthDate = "01/01/2005",
                idNumber = "00000000000",
                email = "giorgi.giorgadze.1@iliauni.edu.ge",
                mobileNumber1 = "(599) 99-99-99",
                mobileNumber2 = "",
                homeNumber = "",
                currentAddress = "",
                juridicalAddress = "",
                photoUrl = null,
                degree = 1
            ),
            userState = DomainMeUserState(
                billingBalance = "562.50₾",
                libraryBalance = "0",
                newsUnread = 0,
                messagesUnread = 0,
                notificationsUnread = 0
            )
        )

    }

}