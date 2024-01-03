package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import dev.xinto.argos.network.response.attributes.ApiAttributesUserProfile
import kotlinx.serialization.Serializable

typealias ApiResponseUserProfile = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesUser, ApiResponseUserProfileRelationships>>

@Serializable
data class ApiResponseUserProfileRelationships(
    val profiles: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUserProfile>>>
)