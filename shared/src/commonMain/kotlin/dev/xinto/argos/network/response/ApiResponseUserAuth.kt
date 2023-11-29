package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesAuthUser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

typealias ApiResponseUserAuth = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesAuthUser, ApiResponseUserAuthRelationships>>

@Serializable
data class ApiResponseUserAuthRelationships(
    val profiles: ApiResponseRelationship<List<ApiResponseDataWithRelationships<ApiAuthUserProfileAttributes, ApiAuthUserProfileRelationships>>>
)

@Serializable
data class ApiAuthUserProfileAttributes(
    val type: Int,
    val degree: Int,
)

@Serializable
data class ApiAuthUserProfileRelationships(
    val profileStatus: ApiResponseRelationship<ApiResponseData<ApiAuthUserProfileStatusAttributes>>
)

@Serializable
data class ApiAuthUserProfileStatusAttributes(
    val active: Boolean,
    val message: JsonObject? //TODO figure this out
)