package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesChoice
import dev.xinto.argos.network.response.attributes.ApiAttributesGroup
import dev.xinto.argos.network.response.attributes.ApiAttributesScore
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import kotlinx.serialization.Serializable

typealias ApiResponseCourseScores = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesChoice, ApiResponseCourseScoresRelationships>>

@Serializable
data class ApiResponseCourseScoresRelationships(
    val scores: ApiResponseRelationship<List<ApiResponseData<ApiAttributesScore>>>,
    val group: ApiResponseRelationship<ApiResponseDataWithRelationships<ApiAttributesGroup, ApiResponseCourseScoresGroupRelationships>>
)

@Serializable
data class ApiResponseCourseScoresGroupRelationships(
    val lecturers: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUser>>>
)