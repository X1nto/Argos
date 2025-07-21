package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesGroupSchedule
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import kotlinx.serialization.Serializable

typealias ApiResponseGroupSchedule = ApiResponsePaged<List<ApiResponseDataWithRelationships<ApiAttributesGroupSchedule, ApiResponseGroupScheduleRelationships>>>

@Serializable
data class ApiResponseGroupScheduleRelationships(
    val lecturers: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUser>>>
)