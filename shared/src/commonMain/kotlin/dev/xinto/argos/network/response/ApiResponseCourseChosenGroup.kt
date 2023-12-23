package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesCourse
import dev.xinto.argos.network.response.attributes.ApiAttributesGroup
import dev.xinto.argos.network.response.attributes.ApiAttributesUser

typealias ApiResponseCourseChosenGroup = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesGroup, ApiResponseCourseChosenGroupRelationships>>

data class ApiResponseCourseChosenGroupRelationships(
    val lecturers: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUser>>>,
    val course: ApiResponseRelationship<ApiResponseData<ApiAttributesCourse>>
)