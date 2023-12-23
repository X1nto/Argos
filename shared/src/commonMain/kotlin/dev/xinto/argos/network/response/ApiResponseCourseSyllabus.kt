package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesCourse
import dev.xinto.argos.network.response.attributes.ApiAttributesCourseSyllabus
import dev.xinto.argos.network.response.attributes.ApiAttributesMediaFile
import kotlinx.serialization.Serializable

typealias ApiResponseCourseSyllabus = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesCourseSyllabus, ApiResponseCourseSyllabusRelationships>>

@Serializable
data class ApiResponseCourseSyllabusRelationships(
    val course: ApiResponseRelationship<ApiResponseData<ApiAttributesCourse>>,
    val syllabusFile: ApiResponseRelationship<ApiResponseData<ApiAttributesMediaFile>>? = null
)
