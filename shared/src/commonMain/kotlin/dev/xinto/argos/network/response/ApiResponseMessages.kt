package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesMessage
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import kotlinx.serialization.Serializable

typealias ApiResponseMessagesInbox = ApiResponsePaged<List<ApiResponseDataWithRelationships<ApiAttributesMessage, ApiResponseMessageInboxRelationships>>>
typealias ApiResponseMessagesOutbox = ApiResponsePaged<List<ApiResponseDataWithRelationships<ApiAttributesMessage, ApiResponseMessageOutboxRelationships>>>
typealias ApiResponseMessage = ApiResponse<ApiResponseDataWithRelationships<ApiAttributesMessage, ApiResponseMessageRelationships>>

@Serializable
data class ApiResponseMessageInboxRelationships(
    val sender: ApiResponseRelationship<ApiResponseData<ApiAttributesUser>>
)

@Serializable
data class ApiResponseMessageOutboxRelationships(
    val receiver: ApiResponseRelationship<ApiResponseData<ApiAttributesUser>>
)

@Serializable
data class ApiResponseMessageRelationships(
    val sender: ApiResponseRelationship<ApiResponseData<ApiAttributesUser>>,
    val receiver: ApiResponseRelationship<ApiResponseData<ApiAttributesUser>>
)