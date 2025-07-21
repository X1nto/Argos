package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesGroupSchedule
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import kotlinx.serialization.Serializable

typealias ApiResponseGroupWeekSchedule = ApiResponse<List<ApiResponseDataWithRelationships<ApiAttributesGroupSchedule, ApiResponseGroupWeekScheduleRelationships>>>

@Serializable
data class ApiResponseGroupWeekScheduleRelationships(
    val lecturers: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUser>>>
)