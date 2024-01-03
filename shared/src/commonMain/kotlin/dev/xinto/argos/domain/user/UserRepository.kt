package dev.xinto.argos.domain.user

import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.network.request.ApiRequestContact
import dev.xinto.argos.util.formatCurrency
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val argosApi: ArgosApi,
    private val argosAccountManager: ArgosAccountManager
) {

    private val meUserInfo = DomainResponseSource({ argosApi.getUserAuth() }) { state ->
        state.data!!.let { (_, attributes, relationships) ->
            DomainMeUserInfo(
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

    private val meUserState = DomainResponseSource({ argosApi.getUserState() }) { state ->
        state.data!!.attributes.let { attributes ->
            DomainMeUserState(
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

    suspend fun updateUserInfo(info: DomainMeUserInfo): Boolean {
        val response = argosApi.patchUserContactInfo(
            ApiRequestContact(
                mobileNumber = info.mobileNumber1,
                mobileNumber2 = info.mobileNumber2,
                homeNumber = info.homeNumber,
                actualAddress = info.currentAddress
            )
        )
        return response.message == "ok"
    }

    fun observeLoggedIn(): Flow<Boolean> {
        return argosAccountManager.isLoggedIn()
    }

    fun getMeUserInfo() = meUserInfo

    fun getMeUserState() = meUserState

    fun getUserProfile(userId: String): DomainResponseSource<*, DomainUserProfile> {
        return DomainResponseSource(
            fetch = {
                argosApi.getUserProfile(userId)
            },
            transform = {
                it.data!!.let { (_, attributes, relationships) ->
                    val profile = relationships.profiles.data[0]
                    when (profile.type) {
                        "UserProfileStudent" -> {
                            DomainStudentProfile(
                                id = attributes.uid,
                                fullName = attributes.fullName,
                                photoUrl = attributes.photoUrl,
                                email = attributes.email,
                                degree = DomainStudentDegree.entries.first {
                                    it.value == profile.attributes.degree
                                }
                            )
                        }
                        "UserProfileLecturer" -> {
                            DomainLecturerProfile(
                                id = attributes.uid,
                                fullName = attributes.fullName,
                                photoUrl = attributes.photoUrl,
                                email = attributes.email,
                                degree = DomainLecturerDegree.entries.first {
                                    it.value == profile.attributes.degree
                                }
                            )
                        }
                        else -> error("Unsupported type")
                    }
                }

            }
        )
    }

}