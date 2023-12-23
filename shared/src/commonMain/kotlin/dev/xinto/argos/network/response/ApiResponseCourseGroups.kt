package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesGroup
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import dev.xinto.argos.network.response.attributes.ApiAttributesChoiceStatus
import kotlinx.serialization.Serializable

typealias ApiResponseCourseGroups = ApiResponse<List<ApiResponseDataWithRelationships<ApiAttributesGroup, ApiResponseCourseGroupsRelationships>>>

@Serializable
data class ApiResponseCourseGroupsRelationships(
    val lecturers: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUser>>>,
    val choiceStatus: ApiResponseRelationship<ApiResponseData<ApiAttributesChoiceStatus>>
)