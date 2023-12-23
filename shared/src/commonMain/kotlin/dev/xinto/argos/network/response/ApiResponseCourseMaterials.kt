package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesCourseFile
import dev.xinto.argos.network.response.attributes.ApiAttributesMediaFile
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import kotlinx.serialization.Serializable

typealias ApiResponseCourseMaterials = ApiResponsePaged<List<ApiResponseDataWithRelationships<ApiAttributesCourseFile, ApiResponseCourseMaterialsRelationships>>>

@Serializable
data class ApiResponseCourseMaterialsRelationships(
    val mediaFile: ApiResponseRelationship<ApiResponseData<ApiAttributesMediaFile>>,
    val user: ApiResponseRelationship<ApiResponseData<ApiAttributesUser>>
)