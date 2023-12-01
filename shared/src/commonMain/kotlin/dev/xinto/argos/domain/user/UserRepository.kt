package dev.xinto.argos.domain.user

import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.domain.combine
import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.network.ArgosApi
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
                fullName = attributes.fullName,
                photoUrl = attributes.photoUrl,
                email = attributes.email,
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

    fun observeLoggedIn(): Flow<Boolean> {
        return argosAccountManager.isLoggedIn()
    }

    fun getUserInfo() = userInfo

    fun getUserState() = userState

}