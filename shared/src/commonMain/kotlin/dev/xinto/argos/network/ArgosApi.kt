package dev.xinto.argos.network

import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.network.request.ApiRequestAuth
import dev.xinto.argos.network.request.ApiRequestAuthRefresh
import dev.xinto.argos.network.request.ApiRequestContact
import dev.xinto.argos.network.response.ApiResponseAuth
import dev.xinto.argos.network.response.ApiResponseCourseChosenGroup
import dev.xinto.argos.network.response.ApiResponseCourseClassmates
import dev.xinto.argos.network.response.ApiResponseCourseGroupSchedule
import dev.xinto.argos.network.response.ApiResponseCourseGroups
import dev.xinto.argos.network.response.ApiResponseCourseMaterials
import dev.xinto.argos.network.response.ApiResponseCourseScores
import dev.xinto.argos.network.response.ApiResponseCourseSyllabus
import dev.xinto.argos.network.response.ApiResponseEmpty
import dev.xinto.argos.network.response.ApiResponseMessage
import dev.xinto.argos.network.response.ApiResponseMessagesInbox
import dev.xinto.argos.network.response.ApiResponseMessagesOutbox
import dev.xinto.argos.network.response.ApiResponseNews
import dev.xinto.argos.network.response.ApiResponseNotifications
import dev.xinto.argos.network.response.ApiResponseSchedules
import dev.xinto.argos.network.response.ApiResponseSemesters
import dev.xinto.argos.network.response.ApiResponseUserProfile
import dev.xinto.argos.network.response.ApiResponseUserAuth
import dev.xinto.argos.network.response.ApiResponseUserState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ArgosApi(private val argosAccountManager: ArgosAccountManager) {

    suspend fun loginGoogle(googleToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            val response = ktorClient.post("auth/token") {
                contentType(ContentType.Application.Json)
                setBody(
                    ApiRequestAuth(
                        clientId = "2",
                        grantType = "google_id_token",
                        token = googleToken
                    )
                )
            }.body<ApiResponseAuth>()
            saveTokens(response).also { successful ->
                if (successful) {
                    getUserAuth()
                }
            }
        }
    }

    suspend fun getUserAuth(): ApiResponseUserAuth {
        return withContext(Dispatchers.IO) {
            val response = ktorClient.get("auth/user").body<ApiResponseUserAuth>()
            if (response.message == "ok") {
                argosAccountManager.setProfileId(response.data!!.relationships.profiles.data[0].id!!)
            }
            return@withContext response
        }
    }

    suspend fun getUserState(): ApiResponseUserState {
        return withContext(Dispatchers.IO) {
            ktorClient.get("user/state").body()
        }
    }

    suspend fun patchUserContactInfo(contact: ApiRequestContact): ApiResponseEmpty {
        return withContext(Dispatchers.IO) {
            ktorClient.patch("user/contact") {
                contentType(ContentType.Application.Json)
                setBody(contact)
            }.body()
        }
    }

    suspend fun getCurrentSchedule(): ApiResponseSchedules {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/schedules/current").body()
        }
    }

    suspend fun getNotifications(page: Int): ApiResponseNotifications {
        return withContext(Dispatchers.IO) {
            ktorClient.get("notifications") {
                parameter("page", page)
            }.body()
        }
    }

    suspend fun getInboxMessages(semesterId: String, page: Int): ApiResponseMessagesInbox {
        return withContext(Dispatchers.IO) {
            ktorClient.get("messages/inbox") {
                parameter("semId", semesterId)
                parameter("page", page)
            }.body()
        }
    }

    suspend fun getOutboxMessages(semesterId: String, page: Int): ApiResponseMessagesOutbox {
        return withContext(Dispatchers.IO) {
            ktorClient.get("messages/outbox") {
                parameter("semId", semesterId)
                parameter("page", page)
            }.body()
        }
    }

    suspend fun getMessage(id: String, semesterId: String): ApiResponseMessage {
        return withContext(Dispatchers.IO) {
            ktorClient.get("messages/${id}") {
                parameter("semId", semesterId)
            }.body()
        }
    }

    suspend fun getSemesters(): ApiResponseSemesters {
        return withContext(Dispatchers.IO) {
            ktorClient.get("user/semesters/available").body()
        }
    }

    suspend fun getNews(page: Int): ApiResponseNews {
        return withContext(Dispatchers.IO) {
            ktorClient.get("news") {
                parameter("page", page)
            }.body()
        }
    }

    suspend fun getNews(id: String): ApiResponseNews {
        return withContext(Dispatchers.IO) {
            ktorClient.get("news/${id}").body()
        }
    }

    suspend fun getCourseSyllabus(courseId: String): ApiResponseCourseSyllabus {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/syllabus").body()
        }
    }

    suspend fun getCourseGroups(courseId: String): ApiResponseCourseGroups {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/groups").body()
        }
    }

    suspend fun getCourseClassmates(courseId: String): ApiResponseCourseClassmates {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/classmates").body()
        }
    }

    suspend fun getCourseScores(courseId: String): ApiResponseCourseScores {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/scores").body()
        }
    }

    suspend fun getCourseChosenGroup(courseId: String): ApiResponseCourseChosenGroup {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/chosen-group").body()
        }
    }

    suspend fun getCourseGroupSchedule(courseId: String, groupId: String): ApiResponseCourseGroupSchedule {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/groups/${groupId}/schedule").body()
        }
    }

    suspend fun getCourseMaterials(courseId: String, page: Int): ApiResponseCourseMaterials {
        return withContext(Dispatchers.IO) {
            ktorClient.get("student/courses/${courseId}/materials") {
                parameter("page", page)
            }.body()
        }
    }

    suspend fun getUserProfile(userId: String): ApiResponseUserProfile {
        return withContext(Dispatchers.IO) {
            ktorClient.get("users/$userId").body()
        }
    }

    private inline fun saveTokens(response: ApiResponseAuth): Boolean {
        if (response.message == "ok") {
            val data = response.data!!
            argosAccountManager.setRefreshToken(data.attributes.refreshToken)
            argosAccountManager.setToken(data.attributes.accessToken)
            return true
        }

        return false
    }

    private val ProfilePlugin = createClientPlugin("ArgusProfile") {
        onRequest { request, _ ->
            val path = request.url.encodedPath
            if (!path.contains("auth/user") && !path.contains("auth/token")) {
                request.headers.append("profile-id", argosAccountManager.getProfileId()!!)
            }
        }
    }

    private val ktorClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpCache) {
            privateStorage(CacheStorage.Disabled)
        }
        defaultRequest {
            url("https://argus.iliauni.edu.ge/api/v1/")

            headers.append("accept-language", "en")
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = argosAccountManager.getToken()
                    val refreshToken = argosAccountManager.getRefreshToken()
                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else null
                }
                refreshTokens {
                    val refreshResponse = client.post("auth/token/refresh") {
                        markAsRefreshTokenRequest()
                        contentType(ContentType.Application.Json)
                        setBody(ApiRequestAuthRefresh(
                            clientId = "2",
                            grantType = "internal_refresh_token",
                            refreshToken = oldTokens?.refreshToken ?: argosAccountManager.getRefreshToken()!!
                        ))
                    }.body<ApiResponseAuth>()
                    if (saveTokens(refreshResponse)) {
                        BearerTokens(
                            accessToken = refreshResponse.data!!.attributes.accessToken,
                            refreshToken = refreshResponse.data.attributes.refreshToken,
                        )
                    } else null
                }
            }
        }
        install(ProfilePlugin)
    }

    companion object
}

internal expect val ArgosApi.Companion.engine: HttpClientEngineFactory<*>