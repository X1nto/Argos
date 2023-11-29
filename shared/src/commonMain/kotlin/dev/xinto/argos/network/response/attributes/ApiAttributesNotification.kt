package dev.xinto.argos.network.response.attributes

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesNotification(
    val type: String,
    val title: String,
    val text: String,
    val data: ApiAttributesNotificationData,
    val readAt: Instant?,
    val createdAt: Instant
)

@Serializable
data class ApiAttributesNotificationData(
    val url: String,
    val semId: Int,
    val courseId: Int? = null,
    val newsId: Int? = null
)