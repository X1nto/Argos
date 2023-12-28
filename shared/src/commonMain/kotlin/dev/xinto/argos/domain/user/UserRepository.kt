package dev.xinto.argos.domain.user

import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.domain.combine
import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.network.request.ApiRequestContact
import dev.xinto.argos.util.formatCurrency
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val argosApi: ArgosApi,
    private val argosAccountManager: ArgosAccountManager
) {

    private val userInfo = DomainResponseSource({ argosApi.getUserAuth() }) { state ->
        state.data!!.let { (_, attributes, relationships) ->
            DomainUserInfo(
                firstName = attributes.firstName,
                lastName = attributes.lastName,
                fullName = attributes.fullName,
                birthDate = attributes.birthDate,
                idNumber = attributes.personalNumber,
                email = attributes.email,
                mobileNumber1 = attributes.mobileNumber,
                mobileNumber2 = attributes.mobileNumber2,
                homeNumber = attributes.homeNumber,
                juridicalAddress = attributes.juridicalAddress,
                currentAddress = attributes.actualAddress,
                photoUrl = attributes.photoUrl,
                degree = relationships.profiles.data[0].attributes.degree,
            )
        }
    }

    private val userState = DomainResponseSource({ argosApi.getUserState() }) { state ->
        state.data!!.attributes.let { attributes ->
            DomainUserState(
                billingBalance = attributes.billingBalance.formatCurrency("GEL"),
                libraryBalance = attributes.libraryBalance.toString(),
                newsUnread = attributes.newsUnread,
                messagesUnread = attributes.messagesUnread,
                notificationsUnread = attributes.notificationsUnread
            )
        }
    }


    suspend fun login(googleIdToken: String): Boolean {
        return argosApi.loginGoogle(googleIdToken)
    }

    suspend fun logout() {
        argosAccountManager.logout()
    }

    suspend fun updateUserInfo(domainUserInfo: DomainUserInfo): Boolean {
        val response = argosApi.patchUserContactInfo(
            ApiRequestContact(
                mobileNumber = domainUserInfo.mobileNumber1,
                mobileNumber2 = domainUserInfo.mobileNumber2,
                homeNumber = domainUserInfo.homeNumber,
                actualAddress = domainUserInfo.currentAddress
            )
        )
        return response.message == "ok"
    }

    fun observeLoggedIn(): Flow<Boolean> {
        return argosAccountManager.isLoggedIn()
    }

    fun getUserInfo() = userInfo

    fun getUserState() = userState

}