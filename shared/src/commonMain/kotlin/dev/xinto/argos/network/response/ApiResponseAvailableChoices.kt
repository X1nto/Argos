package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesAvailableChoiceSummary
import dev.xinto.argos.network.response.attributes.ApiAttributesChoice
import dev.xinto.argos.network.response.attributes.ApiAttributesCourse
import kotlinx.serialization.Serializable

typealias ApiResponseAvailableChoices = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesAvailableChoiceSummary, ApiResponseAvailableChoicesRelationships>>

@Serializable
data class ApiResponseAvailableChoicesRelationships(
    val choices: ApiResponseRelationship<List<ApiResponseDataWithRelationships<ApiAttributesChoice, ApiResponseAvailableChoicesChoiceHistoryRelationships>>>
)

@Serializable
data class ApiResponseAvailableChoicesChoiceHistoryRelationships(
    val course: ApiResponseRelationship<ApiResponseData<ApiAttributesCourse>>
)

