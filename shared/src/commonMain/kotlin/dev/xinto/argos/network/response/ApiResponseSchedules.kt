package dev.xinto.argos.network.response

import dev.xinto.argos.network.response.attributes.ApiAttributesCourse
import dev.xinto.argos.network.response.attributes.ApiAttributesGroup
import dev.xinto.argos.network.response.attributes.ApiAttributesHour
import dev.xinto.argos.network.response.attributes.ApiAttributesSchedule
import dev.xinto.argos.network.response.attributes.ApiAttributesScheduleDay
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import kotlinx.serialization.Serializable

typealias ApiResponseSchedules = ApiResponse<List<ApiResponseDataWithRelationships<ApiAttributesScheduleDay, ApiResponseSchedulesRelationships>>>

@Serializable
data class ApiResponseSchedulesRelationships(
    val schedules: ApiResponseRelationship<List<ApiResponseDataWithRelationships<ApiAttributesSchedule, ApiResponseScheduleRelationships>>>,
)

@Serializable
data class ApiResponseScheduleRelationships(
    val hour: ApiResponseRelationship<ApiResponseData<ApiAttributesHour>>,
    val course: ApiResponseRelationship<ApiResponseData<ApiAttributesCourse>>,
    val group: ApiResponseRelationship<ApiResponseDataWithPossibleRelationships<ApiAttributesGroup, ApiResponseScheduleGroupRelationships>>
)

@Serializable
data class ApiResponseScheduleGroupRelationships(
    val lecturers: ApiResponseRelationship<List<ApiResponseData<ApiAttributesUser>>>
)