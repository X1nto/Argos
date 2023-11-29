package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesMediaFile
import dev.xinto.argos.network.response.attributes.ApiAttributesNews
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

typealias ApiResponseNews = ApiResponsePaged<List<ApiResponseDataWithRelationships<ApiAttributesNews, ApiResponseNewsRelationships>>>
typealias ApiResponseNewsSpecific = ApiResponsePaged<ApiResponseDataWithRelationships<ApiAttributesNews, ApiResponseNewsSpecificRelationships>>

@Serializable
data class ApiResponseNewsRelationships(
    val userViewStatus: ApiResponseRelationship<ApiResponseData<ApiResponseNewsUserViewStatusAttributes>>
)

@Serializable
data class ApiResponseNewsSpecificRelationships(
    val userViewStatus: ApiResponseRelationship<ApiResponseData<ApiResponseNewsUserViewStatusAttributes>>,
    val files: ApiResponseRelationship<ApiResponseData<ApiResponseDataWithRelationships<List<JsonObject>, ApiResponseNewsFileRelationships>>>
)

@Serializable
data class ApiResponseNewsFileRelationships(
    val mediaFile: ApiResponseData<ApiAttributesMediaFile>
)

@Serializable
data class ApiResponseNewsUserViewStatusAttributes(
    val isViewed: Boolean
)