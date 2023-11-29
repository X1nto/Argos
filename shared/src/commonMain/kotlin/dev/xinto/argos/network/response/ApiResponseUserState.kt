package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesUserState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

typealias ApiResponseUserState = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesUserState, ApiResponseUserStateRelationships>>

@Serializable
data class ApiResponseUserStateRelationships(
    val regime: ApiResponseRelationship<ApiResponseData<ApiResponseUserStateRegimeAttributes>>
)

@Serializable
data class ApiResponseUserStateRegimeAttributes(
    val name: String,
    val functionalIsLimited: Boolean,
    val chooseAllowed: Boolean,
    val message: JsonObject? = null //TODO figure this out
)