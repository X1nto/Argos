package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesChoice
import dev.xinto.argos.network.response.attributes.ApiAttributesCourse
import kotlinx.serialization.Serializable

typealias ApiResponseChoices = ApiResponse<List<ApiResponseDataWithRelationships<ApiAttributesChoice, ApiResponseChoicesRelationships>>>

@Serializable
data class ApiResponseChoicesRelationships(
    val course: ApiResponseRelationship<ApiResponseData<ApiAttributesCourse>>
)