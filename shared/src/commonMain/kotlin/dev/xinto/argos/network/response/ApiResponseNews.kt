package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesMediaFile
import dev.xinto.argos.network.response.attributes.ApiAttributesNews
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

typealias ApiResponseAllNews = ApiResponsePaged<List<ApiResponseDataWithRelationships<ApiAttributesNews, ApiResponseNewsRelationships>>>
typealias ApiResponseNews = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesNews, ApiResponseNewsSpecificRelationships>>

@Serializable
data class ApiResponseNewsRelationships(
    val userViewStatus: ApiResponseRelationship<ApiResponseData<ApiResponseNewsUserViewStatusAttributes>>
)

@Serializable
data class ApiResponseNewsSpecificRelationships(
    val userViewStatus: ApiResponseRelationship<ApiResponseData<ApiResponseNewsUserViewStatusAttributes>>,
    val files: ApiResponseRelationship<List<ApiResponseDataWithRelationships<List<JsonObject>, ApiResponseNewsFileRelationships>>>
)

@Serializable
data class ApiResponseNewsFileRelationships(
    val mediaFile: ApiResponseRelationship<ApiResponseData<ApiAttributesMediaFile>>
)

@Serializable
data class ApiResponseNewsUserViewStatusAttributes(
    val isViewed: Boolean
)