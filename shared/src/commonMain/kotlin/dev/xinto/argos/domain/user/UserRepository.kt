package dev.xinto.argos.domain.user

import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.domain.combine
import dev.xinto.argos.network.ArgosApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class UserRepository(
    private val argosApi: ArgosApi
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
                billingBalance = attributes.billingBalance.toString(),
                libraryBalance = attributes.libraryBalance.toString(),
                newsUnread = attributes.newsUnread,
                messagesUnread = attributes.messagesUnread,
                notificationsUnread = attributes.notificationsUnread
            )
        }
    }

    fun observeUserInfo() = userInfo.asFlow()
    private suspend fun refreshUserInfo() = userInfo.refresh()

    fun observeUserState() = userState.asFlow()
    private suspend fun refreshUserState() = userState.refresh()

    fun observeUser() = combine(observeUserInfo(), observeUserState()) { info, state -> info to state }
    suspend fun refreshUser() = coroutineScope {
        listOf(
            async { refreshUserState() },
            async { refreshUserInfo() }
        ).awaitAll()
    }

}