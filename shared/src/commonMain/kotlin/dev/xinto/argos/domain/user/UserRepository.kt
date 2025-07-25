package dev.xinto.argos.domain.user

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.network.request.ApiRequestContact
import dev.xinto.argos.util.FormattedLocalDateTime
import dev.xinto.argos.util.formatCurrency
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val argosApi: ArgosApi,
    private val argosAccountManager: ArgosAccountManager
) {

    val meUserInfo = DomainResponseSource({ argosApi.getUserAuth() }) { state ->
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

    val meUserState = DomainResponseSource({ argosApi.getUserState() }) { state ->
        state.data!!.attributes.let { attributes ->
            //FIXME don't use 0 for nulls
            DomainMeUserState(
                billingBalance = (attributes.billingBalance ?: 0).formatCurrency("GEL"),
                libraryBalance = attributes.libraryBalance.toString(),
                newsUnread = attributes.newsUnread ?: 0,
                messagesUnread = attributes.messagesUnread ?: 0,
                notificationsUnread = attributes.notificationsUnread ?: 0
            )
        }
    }

    suspend fun login(googleIdToken: String): Boolean {
        try {
            return argosApi.loginGoogle(googleIdToken)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun logout() {
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

    fun getUserAuthorizations(): DomainPagedResponsePager<*, DomainUserAuthorization> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getAuthLogs(page)
            },
            transform = {
                it.data!!.map { (id, attributes) ->
                    DomainUserAuthorization(
                        id = id!!,
                        ip = attributes.ip,
                        client = attributes.userAgent,
                        date = FormattedLocalDateTime(attributes.createdAt)
                    )
                }
            }
        )
    }

}