package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesChoiceStatus
import dev.xinto.argos.network.response.attributes.ApiAttributesCourse
import kotlinx.serialization.Serializable

typealias ApiResponseCourse = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesCourse, ApiResponseCourseRelationships>>

@Serializable
data class ApiResponseCourseRelationships(
    val choiceStatus: ApiResponseRelationship<ApiResponseData<ApiAttributesChoiceStatus>>
)